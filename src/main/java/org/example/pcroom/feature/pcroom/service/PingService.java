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


    /**
     * 메인 Ping 실행 메서드
     */
    @Transactional
    public PingUtilizationDto.UtilizationAndResults ping(Long pcroomId) throws ExecutionException, InterruptedException {

        LocalDateTime now = LocalDateTime.now();

        // 1분 이상 지난 경우만 Ping 수행
        List<Seat> seats = seatRepository.findByPcroomId(pcroomId);

        // 좌석 IP 리스트
        List<String> ipList = seats.stream()
                .map(Seat::getSeatsIp)
                .toList();

        // IP → Seat 매핑
        Map<String, Seat> ipToSeat = seats.stream()
                .collect(Collectors.toMap(Seat::getSeatsIp, Function.identity()));

        log.info("ping 작업 시작");


        return performParallelPing(ipList, ipToSeat, pcroomId, now);

    }

    private PingUtilizationDto.UtilizationAndResults performParallelPing(
            List<String> ipList,
            Map<String, Seat> ipToSeat,
            Long pcroomId,
            LocalDateTime now
    ) throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(ipList.size(), 50));
        List<Future<IpResult>> futures = new ArrayList<>();

        for (String ip : ipList) {
            futures.add(executor.submit(() -> {
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

        // Future 결과 수집
        List<IpResult> results = new ArrayList<>();
        for (Future<IpResult> f : futures) {
            IpResult res = f.get();
            if (res != null) results.add(res);
        }

        executor.shutdown();

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

        return latestSeats.stream()
                .map(ipResult -> {
                    Seat seat = seatRepository.findById(ipResult.getSeatId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "좌석 정보를 찾을 수 없습니다. seatId=" + ipResult.getSeatId()));

                    return new IpResultDto.SeatStatusDto(seat.getSeatsNum(), ipResult.getResult());
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
            // IpResult에 utilizationId 주입 후 저장
            r.setUtilizationId(utilizationId);
            ipResultRepository.save(r);
        }
    }
}