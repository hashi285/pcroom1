package org.example.pcroom.feature.pcroom.service;


import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class PingService {

    private final PcroomRepository pcroomRepository;
    private final SeatRepository seatRepository;
    private final IpResultRepository ipResultRepository;

    /**
     * 특정 PC방 ID에 해당하는 좌석 IP들에 대해 ping을 보내고
     * 응답 상태를 저장한 뒤, 결과 메시지를 반환한다.
     */
    @Transactional
    public List<String> update(Long pcroomId) throws Exception {

        // 1. pcroomId를 기준으로 PC방 정보와 좌석들을 가져옴
        Pcroom pcroom = pcroomRepository.findByPcroomIdWithSeat(pcroomId)
                .orElseThrow(() -> new IllegalArgumentException("PC방 없음: " + pcroomId));

        // 2. 좌석 리스트 꺼내기
        List<Seat> seatList = pcroom.getSeats();

        // 3. IP 주소를 키로 하는 Seat 맵 생성 (IP로 좌석을 빠르게 조회하기 위해)
        Map<String, Seat> ipToSeat = seatList.stream()
                .collect(Collectors.toMap(Seat::getSeatsIp, Function.identity()));

        // 4. IP 리스트만 따로 뽑음
        List<String> ipList = seatList.stream()
                .map(Seat::getSeatsIp)
                .toList();

        // 5. ping 성공/실패 카운터
        AtomicInteger aliveCount = new AtomicInteger();
        AtomicInteger deadCount = new AtomicInteger();

        // 6. IP 개수만큼 스레드 풀 생성
        ExecutorService executor = Executors.newFixedThreadPool(ipList.size());

        List<Future<String>> futures = new ArrayList<>();

        // 7. 각 IP에 대해 병렬로 ping 수행
        for (String ip : ipList) {
            futures.add(executor.submit(() -> {

                // 7-1. IP 주소로 ping 시도 (1초 타임아웃)
                boolean isAlive = InetAddress.getByName(ip).isReachable(1000);

                // 7-2. 해당 IP에 매칭되는 Seat 객체 가져옴
                Seat seat = ipToSeat.get(ip);
                if (seat == null) {
                    return ip + ": Seat 정보 없음";
                }

                // 7-3. IpResult 객체 생성 및 저장
                IpResult ipResult = new IpResult();
                ipResult.setResult(isAlive);     // ping 결과
                ipResult.setPcroom(pcroom);      // 어떤 PC방인지
                ipResult.setSeat(seat);          // 어떤 좌석인지
                ipResultRepository.save(ipResult);

                // 7-4. 카운터 증가
                if (isAlive) {
                    aliveCount.getAndIncrement();
                } else {
                    deadCount.getAndIncrement();
                }

                // 7-5. 결과 문자열 반환
                return (isAlive ? "alive" : "dead");
            }));
        }

        // 8. 모든 ping 작업 결과 수집
        List<String> results = new ArrayList<>();
        for (Future<String> future : futures) {
            results.add(future.get()); // 각 스레드가 반환한 결과 받기
        }

        // 9. 스레드풀 종료
        executor.shutdown();

        // 10. 콘솔 출력 (개발용 로그)
        System.out.println("실행 : " + aliveCount + "  미실행 : " + deadCount);

        // 11. 결과 리스트 반환
        return results;
    }
}
