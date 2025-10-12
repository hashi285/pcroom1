package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Table(name = "seat_usage_daily")
@NoArgsConstructor
@AllArgsConstructor
public class SeatUsageDaily {
    @Id
    @GeneratedValue
    private Long id;
    private Long seatId;
    private Long pcroomId;
    private LocalDate date;
    private double usedPercent;
    private LocalDateTime createdAt;
}