package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "utilization")
public class Utilization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long utilizationId;

    @Column(nullable = false)
    private double utilization;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private Long pcroomId;
}
