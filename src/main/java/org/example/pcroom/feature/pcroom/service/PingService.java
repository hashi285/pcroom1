package org.example.pcroom.feature.pcroom.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pcroom.feature.pcroom.dto.IpResultDto;
import org.example.pcroom.feature.pcroom.dto.PingUtilizationDto;
import org.example.pcroom.feature.pcroom.entity.IpResult;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.entity.Utilization;
import org.example.pcroom.feature.pcroom.repository.IpResultRepository;
import org.example.pcroom.feature.pcroom.repository.SeatRepository;
import org.example.pcroom.feature.pcroom.repository.UtilizationRepository;
import org.example.pcroom.global.config.redis.PcroomSeatStatusCacheRepository;
import org.example.pcroom.global.config.redis.PcroomStatusCacheRepository;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PingService {

    private final UtilizationRepository utilizationRepository;
    private final SeatRepository seatRepository;
    private final IpResultRepository ipResultRepository;
    private final PcroomStatusCacheRepository pcroomStatusCacheRepository;
    private final PcroomSeatStatusCacheRepository pcroomSeatStatusCacheRepository;

    // isReachable()의 네이티브 블로킹으로 인한 Carrier Thread Pinning 방지용 캐시드 풀
    private final ExecutorService pingExecutor = Executors.newCachedThreadPool();

    /**
     * 메인 Ping 실행 메서드
     */
    public PingUtilizationDto.UtilizationAndResults ping(Long pcroomId) throws ExecutionException, InterruptedException {

        LocalDateTime now = LocalDateTime.now();

        List<Seat> seats = seatRepository.findByPcroomId(pcroomId);
        
        if (seats.isEmpty()) {
            PingUtilizationDto.UtilizationAndResults emptyResult = new PingUtilizationDto.UtilizationAndResults(
                    new ArrayList<>(),
                    pcroomId,
                    0.0,
                    now
            );
            pcroomStatusCacheRepository.savePcroomStatus(emptyResult);
            pcroomSeatStatusCacheRepository.savePcroomSeatStatus(pcroomId, new ArrayList<>());
            return emptyResult;
        }

        // 좌석 IP 리스트
        List<String> ipList = seats.stream()
                .map(Seat::getSeatsIp)
                .toList();

        // IP → Seat 매핑 (중복 IP가 있을 경우 기존 것 유지)
        Map<String, Seat> ipToSeat = seats.stream()
                .collect(Collectors.toMap(Seat::getSeatsIp, Function.identity(), (existing, replacement) -> existing));

        log.info("ping 작업 시작");


        return performParallelPing(ipList, ipToSeat, pcroomId, now);

    }

    private PingUtilizationDto.UtilizationAndResults performParallelPing(
            List<String> ipList,
            Map<String, Seat> ipToSeat,
            Long pcroomId,
            LocalDateTime now
    ) throws InterruptedException, ExecutionException {

        List<Future<IpResult>> futures = new ArrayList<>();
        List<IpResult> results = new ArrayList<>();

        // 가상 스레드의 OS 네이티브 핀닝(Pinning) 문제로 인해 OS 스레드 풀(CachedThreadPool) 사용
        for (String ip : ipList) {
            futures.add(pingExecutor.submit(() -> {
                boolean isAlive = ping(ip);
                Seat seat = ipToSeat.get(ip);
                if (seat == null) return null;

                IpResult result = new IpResult();
                result.setResult(isAlive);
                result.setPcroomId(pcroomId);
                result.setSeatId(seat.getSeatId());
                return result;
            }));
        }

        // Future 결과 수집 (모든 핑 완료 대기, 최대 2초)
        for (Future<IpResult> f : futures) {
            IpResult res = f.get();
            if (res != null) results.add(res);
        }

        // utilization 계산
        double aliveCount = results.stream().filter(IpResult::getResult).count();
        double utilization = Math.round((aliveCount / results.size() * 100.0) * 100) / 100.0;

        // Redis 저장용 SeatStatusDto 리스트 생성
        List<IpResultDto.SeatStatusDto> seatStatusDtoList = results.stream()
                .map(r -> new IpResultDto.SeatStatusDto(
                        ipToSeat.entrySet().stream()
                                .filter(e -> e.getValue().getSeatId().equals(r.getSeatId()))
                                .findFirst()
                                .map(e -> e.getValue().getSeatsNum())
                                .orElse(null),
                        r.getResult()
                ))
                .toList();

        // 전체 ping + 활용도 DTO
        PingUtilizationDto.UtilizationAndResults result =
                new PingUtilizationDto.UtilizationAndResults(
                        results,
                        pcroomId,
                        utilization,
                        now
                );

        // Redis 저장
        pcroomStatusCacheRepository.savePcroomStatus(result);                // 활용도 + 상세 결과
        pcroomSeatStatusCacheRepository.savePcroomSeatStatus(pcroomId, seatStatusDtoList);  // 좌석만

        return result;
    }

    /**
     * 실제 Ping 수행 (2초 제한)
     */
    private boolean ping(String ip) {
        try {
            return InetAddress.getByName(ip).isReachable(2000);
        } catch (Exception e) {
            return false;
        }
    }


    @Transactional
    public List<IpResultDto.SeatStatusDto> getLatestSeatResults(Long pcroomId) {
        List<IpResult> latestSeats = ipResultRepository.findLatestByPcroomIdBeforeNow(pcroomId, LocalDateTime.now());

        // N+1 문제 해결: 전체 좌석을 한 번에 조회 후 매핑
        List<Seat> pcroomSeats = seatRepository.findByPcroomId(pcroomId);
        Map<Long, Integer> seatNumMap = pcroomSeats.stream()
                .collect(Collectors.toMap(Seat::getSeatId, Seat::getSeatsNum));

        return latestSeats.stream()
                .map(ipResult -> {
                    Integer seatNum = seatNumMap.get(ipResult.getSeatId());
                    if (seatNum == null) {
                        throw new EntityNotFoundException("좌석 정보를 찾을 수 없습니다. seatId=" + ipResult.getSeatId());
                    }
                    return new IpResultDto.SeatStatusDto(seatNum, ipResult.getResult());
                })
                .toList();
    }


    /**
     *
     * @param results  List(seatId, seatNum, isAlive)
     * @param pcroomId 피시방 Id
     * @param now      현재 시각
     * @return 가동률
     */

    @Transactional
    public void saveUtilizationAndResults(List<IpResult> results, Long pcroomId, double utilizationRate, LocalDateTime now) {


        // Utilization 저장
        Utilization utilization = new Utilization();
        utilization.setPcroomId(pcroomId);
        utilization.setTime(now);
        utilization.setUtilization(utilizationRate);
        utilizationRepository.save(utilization);
        Long utilizationId = utilization.getUtilizationId();

        for (IpResult r : results) {
            r.setUtilizationId(utilizationId);
        }
        
        // Batch Insert 적용
        ipResultRepository.saveAll(results);
    }
}