package org.example.pcroom.feature.pcroom.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SeatDailyUsageWithInfoDto {
    private Long seatId;
    private Integer seatNum;
    private String seatsIp;
    private int x;
    private int y;
    private double usedPercent; // 사용률 %
    private LocalDate date; // 해당 날짜
}