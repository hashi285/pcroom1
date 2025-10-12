package org.example.pcroom.feature.pcroom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.SeatUsageDailyResponse;
import org.example.pcroom.feature.pcroom.entity.IpResult;
import org.example.pcroom.feature.pcroom.entity.SeatUsageHourly;
import org.example.pcroom.feature.pcroom.repository.SeatUsageDailyRepository;
import org.example.pcroom.feature.pcroom.repository.SeatUsageHourlyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatUsageService {

    private final SeatUsageDailyRepository seatUsageDailyRepository;
    private final SeatUsageHourlyRepository seatUsageHourlyRepository;

    /**
     * 하루 단위 사용률 조회
     */
    @Transactional
    public List<SeatUsageDailyResponse> getDailyUsage(Long pcroomId, LocalDate start, LocalDate end) {
        return seatUsageDailyRepository.findAllByPcroomIdAndDateBetween(pcroomId, start, end)
                .stream()
                .map(data -> SeatUsageDailyResponse.builder()
                        .seatId(data.getSeatId())
                        .pcroomId(data.getPcroomId())
                        .date(data.getDate())
                        .usedPercent(data.getUsedPercent())
                        .build())
                .toList();
    }

    /**
     * 30분마다 호출: IpResult 기반으로 시간 단위 좌석 사용시간(초) 누적
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
