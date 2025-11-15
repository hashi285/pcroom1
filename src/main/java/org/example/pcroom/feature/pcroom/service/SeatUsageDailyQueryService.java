package org.example.pcroom.feature.pcroom.service;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.entity.SeatDailyUsageWithInfoDto;
import org.example.pcroom.feature.pcroom.entity.SeatUsageDaily;
import org.example.pcroom.feature.pcroom.repository.SeatRepository;
import org.example.pcroom.feature.pcroom.repository.SeatUsageDailyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatUsageDailyQueryService {

    private final SeatUsageDailyRepository dailyRepository;
    private final SeatRepository seatRepository;

    public List<SeatDailyUsageWithInfoDto> getDailyUsageWithSeatInfo(Long pcroomId, LocalDate startDate, LocalDate endDate) {
        // 1. 좌석 정보 조회
        List<Seat> seats = seatRepository.findByPcroomId(pcroomId);
        Map<Long, Seat> seatMap = seats.stream()
                .collect(Collectors.toMap(Seat::getSeatId, s -> s));

        // 2. Daily 사용률 조회
        List<SeatUsageDaily> dailyRecords = dailyRepository.findByPcroomIdAndDateBetween(pcroomId, startDate, endDate);

        // 3. DTO 변환
        return dailyRecords.stream()
                .map(r -> {
                    Seat seat = seatMap.get(r.getSeatId());
                    if (seat == null) return null; // 좌석 정보 없으면 무시
                    return new SeatDailyUsageWithInfoDto(
                            seat.getSeatId(),
                            seat.getSeatsNum(),
                            seat.getSeatsIp(),
                            seat.getX(),
                            seat.getY(),
                            r.getUsedPercent(),
                            r.getDate()
                    );
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
