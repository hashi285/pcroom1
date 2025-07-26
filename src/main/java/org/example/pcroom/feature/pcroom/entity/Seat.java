package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "seat_num", nullable = false)
    private Integer seatsNum;

    @Column(name = "seat_ip", nullable = false, length = 45)
    private String seatsIp;

    @Column(name = "zone_number", nullable = false)
    private int zoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcroom_id", nullable = false)
    private Pcroom pcroom;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpResult> ipResults;
}