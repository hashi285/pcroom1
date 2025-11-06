package org.example.pcroom.feature.pcroom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.entity.IpResult;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.entity.Utilization;
import org.example.pcroom.feature.pcroom.repository.IpResultRepository;
import org.example.pcroom.feature.pcroom.repository.SeatRepository;
import org.example.pcroom.feature.pcroom.repository.UtilizationRepository;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PingService {

    private final UtilizationRepository utilizationRepository;
    private final SeatRepository seatRepository;
    private final IpResultRepository ipResultRepository;
    private final SeatUsageService seatUsageService;

    /**
     * 메인 Ping 실행 메서드
     */
    @Transactional
    public double ping(Long pcroomId) throws ExecutionException, InterruptedException {

        Optional<Utilization> utilization = utilizationRepository.findTopByPcroomIdOrderByTimeDesc(pcroomId);

        // DB에 기록이 없으면 고정값, 있으면 DB값 사용
        LocalDateTime lastTime = utilization
                .map(Utilization::getTime)
                .orElse(LocalDateTime.of(2024, 9, 14, 15, 30, 0));

        LocalDateTime now = LocalDateTime.now();

        // 1분 이상 지난 경우만 Ping 수행
        if (lastTime.isBefore(now.minusMinutes(1))) {
            List<Seat> seats = seatRepository.findByPcroomId(pcroomId);

            // 좌석 IP 리스트
            List<String> ipList = seats.stream()
                    .map(Seat::getSeatsIp)
                    .toList();

            // IP → Seat 매핑
            Map<String, Seat> ipToSeat = seats.stream()
                    .collect(Collectors.toMap(Seat::getSeatsIp, Function.identity()));

            System.out.println("Ping 수행 시작");
            return performParallelPing(ipList, ipToSeat, pcroomId, now);
        } else {
            System.out.println("최근 데이터 재활용");
            return utilization.get().getUtilization();
        }
    }

    /**
     * 병렬로 Ping 수행 + 결과 수집
     */
    private double performParallelPing(List<String> ipList, Map<String, Seat> ipToSeat,
                                       Long pcroomId, LocalDateTime now)
            throws InterruptedException, ExecutionException {

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

        return saveUtilizationAndResults(results, pcroomId, now);
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

    /**
     * Utilization + IpResult 저장
     */
    @Transactional
    protected double saveUtilizationAndResults(List<IpResult> results, Long pcroomId, LocalDateTime now) {
        if (results.isEmpty()) return 0.0;

        // 살아있는 좌석 수 계산
        double aliveCount = results.stream().filter(IpResult::getResult).count();
        double utilizationRate = Math.round((aliveCount / results.size() * 100.0) * 100) / 100.0;

        // Utilization 저장
        Utilization utilization = new Utilization();
        utilization.setPcroomId(pcroomId);
        utilization.setTime(now);
        utilization.setUtilization(utilizationRate);
        utilizationRepository.save(utilization);

        Long utilizationId = utilization.getUtilizationId();

        // 30분 단위 체크
        int minute = now.getMinute();
        if (minute % 30 == 0) {
            for (IpResult r : results) {
                // IpResult에 utilizationId 주입 후 저장
                r.setUtilizationId(utilizationId);
                ipResultRepository.save(r);

                // 좌석별 사용시간 누적
                seatUsageService.updateHourlySeatUsage(
                        Collections.singletonList(r), pcroomId, now
                );
            }
        }

        return utilizationRate;
    }
}