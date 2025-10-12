package org.example.pcroom.feature.pcroom.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.service.PingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PingScheduler {

    private final PingService pingService;
    private final PcroomRepository pcroomRepository;

    /**
     * 3분마다 모든 PC방 ping 수행
     */
    @Scheduled(cron = "0 0,30 * * * *")
    public void pingAllPcrooms() {
        List<Long> pcroomIds = pcroomRepository.findAll()
                .stream()
                .map(Pcroom::getPcroomId)
                .toList();

        for (Long pcroomId : pcroomIds) {
            try {
                pingService.ping(pcroomId);
                System.out.println("Ping 완료: PC방 " + pcroomId);
            } catch (Exception e) {
                System.err.println("Ping 실패: PC방 " + pcroomId + " / " + e.getMessage());
            }
        }
    }
}
