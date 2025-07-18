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

    @Transactional
    public List<String> update(Long pcroomId) throws Exception {
        Pcroom pcroom = fetchPcroomWithSeats(pcroomId);
        List<Seat> seats = pcroom.getSeats();

        Map<String, Seat> ipToSeat = mapIpToSeat(seats);
        List<String> ipList = extractIpList(seats);

        return performParallelPing(ipList, ipToSeat, pcroom);
    }

    private Pcroom fetchPcroomWithSeats(Long pcroomId) {
        return pcroomRepository.findByPcroomIdWithSeat(pcroomId)
                .orElseThrow(() -> new IllegalArgumentException("PC방 없음: " + pcroomId));
    }

    private Map<String, Seat> mapIpToSeat(List<Seat> seats) {
        return seats.stream()
                .collect(Collectors.toMap(Seat::getSeatsIp, Function.identity()));
    }

    private List<String> extractIpList(List<Seat> seats) {
        return seats.stream()
                .map(Seat::getSeatsIp)
                .toList();
    }

    private List<String> performParallelPing(List<String> ipList, Map<String, Seat> ipToSeat, Pcroom pcroom) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(ipList.size());

        List<Future<String>> futures = new ArrayList<>();
        AtomicInteger aliveCount = new AtomicInteger();
        AtomicInteger deadCount = new AtomicInteger();

        for (String ip : ipList) {
            futures.add(executor.submit(() -> {
                boolean isAlive = ping(ip);
                Seat seat = ipToSeat.get(ip);

                if (seat != null) {
                    saveIpResult(isAlive, pcroom, seat);
                }

                if (isAlive) {
                    aliveCount.incrementAndGet();
                } else {
                    deadCount.incrementAndGet();
                }

                return isAlive ? "alive" : "dead";
            }));
        }

        List<String> results = collectResults(futures);
        executor.shutdown();

        logResultCounts(aliveCount.get(), deadCount.get());
        return results;
    }

    private boolean ping(String ip) throws Exception {
        return InetAddress.getByName(ip).isReachable(1000);
    }

    private void saveIpResult(boolean isAlive, Pcroom pcroom, Seat seat) {
        IpResult ipResult = new IpResult();
        ipResult.setResult(isAlive);
        ipResult.setPcroom(pcroom);
        ipResult.setSeat(seat);
        ipResultRepository.save(ipResult);
    }

    private List<String> collectResults(List<Future<String>> futures) throws InterruptedException, ExecutionException {
        List<String> results = new ArrayList<>();
        for (Future<String> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    private void logResultCounts(int alive, int dead) {
        System.out.println("실행 : " + alive + "  미실행 : " + dead);
    }
}
