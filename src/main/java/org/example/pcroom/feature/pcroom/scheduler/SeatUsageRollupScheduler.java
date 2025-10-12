package org.example.pcroom.feature.pcroom.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.SeatUsageDailyDTO;
import org.example.pcroom.feature.pcroom.entity.IpResult;
import org.example.pcroom.feature.pcroom.entity.SeatUsageDaily;
import org.example.pcroom.feature.pcroom.entity.SeatUsageHourly;
import org.example.pcroom.feature.pcroom.repository.SeatUsageDailyRepository;
import org.example.pcroom.feature.pcroom.repository.SeatUsageHourlyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatUsageRollupScheduler {

    private final SeatUsageHourlyRepository seatUsageHourlyRepository;
    private final SeatUsageDailyRepository seatUsageDailyRepository;

    /**
     * 매일 정각에 좌석별 사용량을 계산하여 테이블에 저장
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void rollupDailyUsage() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 1️⃣ 어제 데이터 집계(seatId, pcroomId, totalUsedSeconds)
        List<SeatUsageDailyDTO> aggregates = seatUsageHourlyRepository.aggregateDaily(yesterday);

        // 2️⃣ daily 테이블에 저장
        List<SeatUsageDaily> dailyEntities = aggregates.stream()
                .map(dto -> SeatUsageDaily.builder()
                        .seatId(dto.getSeatId())
                        .pcroomId(dto.getPcroomId())
                        .date(yesterday)
                        .usedPercent((double) dto.getTotalUsedSeconds()/86400 * 100) // aggregates의 사용시간 %로 변환
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        seatUsageDailyRepository.saveAll(dailyEntities);

        // 3️⃣ 시간 단위 데이터 삭제
        seatUsageHourlyRepository.deleteByDate(yesterday);
    }

    /**
     * 30분에 한번씩 ip_result에서 값을 가져와 좌석별 사용시간(초)를 계산 후 seat_usage_hourly에 저장
     */
    @Transactional
    public void updateHourlySeatUsage(List<IpResult> results, Long pcroomId, LocalDateTime now) {
        LocalDate date = now.toLocalDate();
        int hour = now.getHour();

        for (IpResult r : results) {
            Long seatId = r.getSeatId();
            boolean isAlive = r.getResult();

            SeatUsageHourly hourly = seatUsageHourlyRepository
                    .findBySeatIdAndPcroomIdAndDateHour(seatId, pcroomId, date, hour)
                    .orElseGet(() -> SeatUsageHourly.builder()
                            .seatId(seatId)
                            .pcroomId(pcroomId)
                            .usedSeconds(0)
                            .createdAt(now)
                            .build());

            if (isAlive) {
                hourly.setUsedSeconds(hourly.getUsedSeconds() + 1800); // 30분 누적
            }

            seatUsageHourlyRepository.save(hourly);
        }
    }

}
