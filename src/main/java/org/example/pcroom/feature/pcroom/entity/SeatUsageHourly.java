package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "seat_usage_hourly")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SeatUsageHourly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long seatId; //좌석 번호
    private Long pcroomId; // 피시방 번호
    private int usedSeconds;
    private LocalDateTime createdAt;
}
