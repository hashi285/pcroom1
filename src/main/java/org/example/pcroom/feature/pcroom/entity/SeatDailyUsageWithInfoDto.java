package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SeatDailyUsageWithInfoDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(nullable = false)
    private Integer seatNum;

    @Column(nullable = false)
    private String seatsIp;

    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    @Column(nullable = false)
    private double usedPercent; // 사용률 %

    @Column(nullable = false)
    private LocalDate date; // 해당 날짜
}