package org.example.pcroom.feature.manager.service;


import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.manager.entity.PcroomHourlyUtilization;
import org.example.pcroom.feature.manager.repository.PcroomHourlyUtilizationRepository;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.service.PingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PcroomHourlyUtilizationBatchService {

    private final PcroomHourlyUtilizationRepository pcroomHourlyUtilizationRepository;
    private final PcroomRepository pcroomRepository;
    private final PingService pingService; // 기존 ping 서비스

    @Scheduled(cron = "0 0 * * * *") // 매 시 정각마다 실행
    public void saveHourlyUtilization() {
        List<Long> pcroomIds = pcroomRepository.findAllPcroomIds(); // 모든 피시방 ID 조회

        LocalDateTime nowHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        for (Long pcroomId : pcroomIds) {
            // 이미 해당 시간 데이터가 존재하면 skip
            if (pcroomHourlyUtilizationRepository.existsByPcroomIdAndTime(pcroomId, nowHour)) continue;

            double utilization = 0;
            try {
                utilization = pingService.ping(pcroomId); // 0~100% 값 반환
            } catch (Exception e) {
                e.printStackTrace();
            }

            PcroomHourlyUtilization record = new PcroomHourlyUtilization();
            record.setPcroomId(pcroomId);
            record.setUtilization(utilization);
            record.setTime(nowHour);
            pcroomHourlyUtilizationRepository.save(record);
        }
    }
}
