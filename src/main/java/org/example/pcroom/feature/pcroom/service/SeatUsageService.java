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
     * 자리별 사용률 반환
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



}
