package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false)
    private Long pcroomId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double usedPercent;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}