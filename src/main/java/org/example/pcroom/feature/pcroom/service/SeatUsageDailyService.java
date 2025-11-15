package org.example.pcroom.feature.pcroom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.entity.SeatUsageDaily;
import org.example.pcroom.feature.pcroom.entity.SeatUsageHourly;
import org.example.pcroom.feature.pcroom.repository.SeatUsageDailyRepository;
import org.example.pcroom.feature.pcroom.repository.SeatUsageHourlyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatUsageDailyService {

    private final SeatUsageHourlyRepository hourlyRepository;
    private final SeatUsageDailyRepository dailyRepository;

    /**
     * 전날 SeatUsageHourly 데이터를 집계하여 SeatUsageDaily 저장
     */
    @Transactional
    public void aggregateDailyUsage(Long pcroomId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay(); // 00:00:00
        LocalDateTime end = yesterday.atTime(LocalTime.MAX); // 23:59:59.999999

        // 1. 전날 해당 PC방 Hourly 데이터 조회
        List<SeatUsageHourly> hourlyList = hourlyRepository.findByPcroomIdAndCreatedAtBetween(pcroomId, start, end);

        if (hourlyList.isEmpty()) return;

        // 2. 좌석별 사용률 계산
        Map<Long, Double> seatUsagePercent = hourlyList.stream()
                .collect(Collectors.groupingBy(
                        SeatUsageHourly::getSeatId,
                        Collectors.summingInt(SeatUsageHourly::getUsedSeconds) // 초 합계
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.min(100.0, e.getValue() / (double)(24 * 3600) * 100) // 하루 24시간 기준 % 계산
                ));

        LocalDateTime now = LocalDateTime.now();

        // 3. SeatUsageDaily 생성
        List<SeatUsageDaily> dailyList = seatUsagePercent.entrySet().stream()
                .map(e -> SeatUsageDaily.builder()
                        .pcroomId(pcroomId)
                        .seatId(e.getKey())
                        .date(yesterday)
                        .usedPercent(Math.round(e.getValue() * 100.0) / 100.0) // 소수점 2자리
                        .createdAt(now)
                        .build()
                ).toList();

        // 4. DB 저장
        dailyRepository.saveAll(dailyList);
    }
}
