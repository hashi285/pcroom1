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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PingService {
    private final UtilizationRepository utilizationRepository;
    private final SeatRepository seatRepository;
    private final IpResultRepository ipResultRepository;
    private final List<IpResult> buffer = Collections.synchronizedList(new ArrayList<>());


    @Transactional
    public double ping(Long pcroomId) throws ExecutionException, InterruptedException {

       Optional<Utilization> utilization =  utilizationRepository.findTopByPcroomIdOrderByTimeDesc(pcroomId);

// DB에 기록이 없으면 고정값, 있으면 DB값 사용
        LocalDateTime lastTime = utilization
                .map(Utilization::getTime)
                .orElse(LocalDateTime.of(2024, 9, 14, 15, 30, 0));

        LocalDateTime now = LocalDateTime.now();
// 현재 시간과 비교
        if (lastTime.isBefore(now.minusMinutes(1))) {

           List<Seat> seat = seatRepository.findByPcroomId(pcroomId);

           List<String> list = seat.stream()
                   .map(Seat::getSeatsIp)
                   .toList();


           // 좌석 리스트를 IP → SetSeat 매핑 Map으로 변환
           Map<String, Seat> ipToSeat = seat.stream()
                   .collect(Collectors.toMap(Seat::getSeatsIp, Function.identity()));
            System.out.println("성공");

           return performParallelPing(list, ipToSeat, pcroomId, now);

       } else {
            System.out.println("실패");
           return utilization.get().getUtilization();
       }

    }

    // IP 리스트를 병렬로 Ping 수행하고, 좌석 상태 저장 후 결과 문자열 리스트 반환
    private double performParallelPing(List<String> ipList, Map<String, Seat> ipToSeat, Long pcroomId, LocalDateTime now) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(ipList.size()); // 병렬 실행을 위한 스레드 풀

        List<Future<String>> futures = new ArrayList<>(); // Ping 결과 Future 리스트
        AtomicInteger aliveCount = new AtomicInteger();  // 살아있는 좌석 수
        AtomicInteger deadCount = new AtomicInteger();   // 죽은 좌석 수

        for (String ip : ipList) {
            // 각 IP마다 Ping을 수행하는 Callable 제출
            futures.add(executor.submit(() -> {
                boolean isAlive = ping(ip);            // 실제 Ping 수행
                Seat seat = ipToSeat.get(ip);          // IP → SetSeat 매핑

                if (seat != null) {
                    saveIpResult(isAlive, pcroomId, seat); // 결과 저장
                }

                if (isAlive) {
                    aliveCount.incrementAndGet();     // 살아있으면 카운트 증가
                } else {
                    deadCount.incrementAndGet();      // 죽었으면 카운트 증가
                }

                return isAlive ? "1" : "0";    // 결과 문자열 반환
            }));
        }

        // Future 리스트에서 결과 수집
        List<String> results = collectResults(futures);

        executor.shutdown(); // 스레드 풀 종료

        return utilization(results, pcroomId, now); // 가동률 반환
    }

    // 주어진 IP로 실제 Ping 수행
    private boolean ping(String ip) throws Exception {
        return InetAddress.getByName(ip).isReachable(1000); // 1000ms timeout
    }

    // Ping 결과를 IpResult 엔티티에 저장
    private void saveIpResult(boolean isAlive, Long pcroomId, Seat seat) {
        IpResult ipResult = new IpResult();
        ipResult.setResult(isAlive);  // 결과 저장
        ipResult.setPcroomId(pcroomId); // PC방 아이디 저장
        ipResult.setSeatId(seat.getSeatId());    // 좌석 정보 저장
        buffer.add(ipResult);
    }

    // Future 리스트에서 결과 문자열 수집
    private List<String> collectResults(List<Future<String>> futures) throws InterruptedException, ExecutionException {
        List<String> results = new ArrayList<>();
        for (Future<String> future : futures) {
            results.add(future.get()); // Future.get()으로 결과 추출
        }
        return results;
    }

    private double utilization(List<String> utilization,Long pcroomId, LocalDateTime now ){ // 가동률 반환
        double sum = 0;
        for (String a :utilization){
            if (a.equals("1")){
                sum++;
            }
        }
        double result = Math.round((sum / utilization.size() * 100.0) * 100) / 100.0;
        Utilization utilization1 = new Utilization();
        utilization1.setUtilization(result);
        utilization1.setTime(now);
        utilization1.setPcroomId(pcroomId);
        utilizationRepository.save(utilization1);;
        Long id = utilization1.getUtilizationId();
        buffer.forEach(ipResult -> ipResult.setUtilizationId(id));
        ipResultRepository.saveAll(buffer);
        buffer.clear();
        return result;
    }
}
