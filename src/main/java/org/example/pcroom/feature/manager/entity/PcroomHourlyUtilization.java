package org.example.pcroom.feature.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * 피시방 가동률(한시간 텀)
 */
@Entity
@Getter
@Setter
@Table(name = "pcrooms_hourly_utilization")
public class PcroomHourlyUtilization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double utilization; // 한시간 텀

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private Long pcroomId;
}
