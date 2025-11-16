package org.example.pcroom.feature.pcroom.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.repository.SeatUsageHourlyRepository;
import org.example.pcroom.feature.pcroom.service.PingService;
import org.example.pcroom.feature.pcroom.service.SeatUsageDailyService;
import org.example.pcroom.feature.pcroom.service.SeatUsageHourlyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
@Component
@RequiredArgsConstructor
public class PingScheduler {

    private final PingService pingService;
    private final PcroomRepository pcroomRepository;
    private final SeatUsageHourlyService seatUsageHourlyService;
    private final SeatUsageDailyService seatUsageDailyService;

    private final SeatUsageHourlyRepository seatUsageHourlyRepository;
    /**
     * 30분마다 모든 PC방 좌석별 사용량 기록
     */
    @Scheduled(cron = "0 0,30 * * * *") // 매 정각/30분
    public void recordHourlyUsageAllPcrooms() {
        List<Long> pcroomIds = pcroomRepository.findAll()
                .stream()
                .map(Pcroom::getPcroomId)
                .toList();

        for (Long pcroomId : pcroomIds) {
            try {
                // 1. Ping 수행 → IpResult + Utilization 저장
                pingService.ping(pcroomId);

                // 2. 좌석별 사용량 기록
                seatUsageHourlyService.recordHourlyUsage(pcroomId);

                System.out.println("좌석 사용량 기록 완료: PC방 " + pcroomId);
            } catch (Exception e) {
                System.err.println("좌석 사용량 기록 실패: PC방 " + pcroomId + " / " + e.getMessage());
            }
        }
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void aggregateAllPcroomsDaily() {
        List<Long> pcroomIds = pcroomRepository.findAll()
                .stream()
                .map(Pcroom::getPcroomId)
                .toList();

        // 자정 기준으로 '어제' 날짜 계산
        LocalDate targetDate = LocalDate.now().minusDays(1);

        for (Long pcroomId : pcroomIds) {
            // 1. 하루치 집계 수행
            seatUsageDailyService.aggregateDailyUsage(pcroomId);

            // 2. 집계 완료 후 hourly 데이터 삭제
            seatUsageHourlyRepository.deleteByPcroomIdAndDate(pcroomId, targetDate);

            System.out.println("Daily 집계 및 hourly 삭제 완료: " + pcroomId + " / " + targetDate);
        }
    }

}
