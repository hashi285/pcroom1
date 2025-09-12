package org.example.pcroom.feature.pcroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pcroom.feature.pcroom.entity.IpResult;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.repository.IpResultRepository;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PingService {

    private final PcroomRepository pcroomRepository;  // Pcroom 엔티티 DB 접근용 레포지토리
    private final SeatRepository seatRepository;      // SetSeat 엔티티 DB 접근용 레포지토리
    private final IpResultRepository ipResultRepository; // IpResult 엔티티 DB 접근용 레포지토리

    // PC방 ID를 기반으로 좌석들의 IP를 Ping하고 결과 저장 후 상태 리스트 반환
    @Transactional
    public List<String> update(Long pcroomId) throws Exception {

        // pcroomId에 속한 좌석 리스트 조회 (여기서는 top 한 건만 조회하는 메서드 사용)
        List<Seat> seats = seatRepository.findTopByPcroomId(pcroomId);

        // 좌석 리스트를 IP → SetSeat 매핑 Map으로 변환
        Map<String, Seat> ipToSeat = mapIpToSeat(seats);

        // 좌석 리스트에서 IP만 추출
        List<String> ipList = extractIpList(seats);

        // pcroom 가져옴
        Pcroom pcrooms = pcroomRepository.findByPcroomId(pcroomId);

        // 병렬로 Ping 수행하고 결과 수집
        return performParallelPing(ipList, ipToSeat, pcrooms);
    }

//    // pcroomId로 Pcroom 객체와 좌석 정보를 함께 조회
//    private Pcroom fetchPcroomWithSeats(Long pcroomId) {
//        pcroomRepository.findByPcroomId(pcroomId);
//
//        return pcroomRepository.findByPcroomIdWithSeat(pcroomId)
//                .orElseThrow(() -> new IllegalArgumentException("PC방 없음: " + pcroomId));
//    }

    // 좌석 리스트를 IP를 키로 하는 Map으로 변환
    private Map<String, Seat> mapIpToSeat(List<Seat> seats) {
        return seats.stream()
                .collect(Collectors.toMap(Seat::getSeatsIp, Function.identity()));
    }

    // 좌석 리스트에서 IP만 추출하여 리스트로 반환
    private List<String> extractIpList(List<Seat> seats) {
        return seats.stream()
                .map(Seat::getSeatsIp)
                .toList();
    }

    // IP 리스트를 병렬로 Ping 수행하고, 좌석 상태 저장 후 결과 문자열 리스트 반환
    private List<String> performParallelPing(List<String> ipList, Map<String, Seat> ipToSeat, Pcroom pcroom) throws InterruptedException, ExecutionException {
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
                    saveIpResult(isAlive, pcroom, seat); // 결과 저장
                }

                if (isAlive) {
                    aliveCount.incrementAndGet();     // 살아있으면 카운트 증가
                } else {
                    deadCount.incrementAndGet();      // 죽었으면 카운트 증가
                }

                return isAlive ? "alive" : "dead";    // 결과 문자열 반환
            }));
        }

        // Future 리스트에서 결과 수집
        List<String> results = collectResults(futures);

        executor.shutdown(); // 스레드 풀 종료

        // 결과 카운트 로그 출력
        logResultCounts(aliveCount.get(), deadCount.get());

        return results; // 결과 문자열 리스트 반환
    }

    // 주어진 IP로 실제 Ping 수행
    private boolean ping(String ip) throws Exception {
        return InetAddress.getByName(ip).isReachable(1000); // 1000ms timeout
    }

    // Ping 결과를 IpResult 엔티티에 저장
    private void saveIpResult(boolean isAlive, Pcroom pcroom, Seat seat) {
        IpResult ipResult = new IpResult();
        ipResult.setResult(isAlive);  // 결과 저장
        ipResult.setPcroomId(seat.getPcroomId());  // PC방 정보 저장
        ipResult.setSeatId(seat.getSeatId());    // 좌석 정보 저장
        ipResultRepository.save(ipResult); // DB 저장
    }

    // Future 리스트에서 결과 문자열 수집
    private List<String> collectResults(List<Future<String>> futures) throws InterruptedException, ExecutionException {
        List<String> results = new ArrayList<>();
        for (Future<String> future : futures) {
            results.add(future.get()); // Future.get()으로 결과 추출
        }
        return results;
    }

    // alive / dead 좌석 수를 콘솔에 출력
    private void logResultCounts(int alive, int dead) {
        System.out.println("실행 : " + alive + "  미실행 : " + dead);
    }
}
