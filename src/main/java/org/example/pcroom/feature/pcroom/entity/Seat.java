package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * PC방 자리 정보

 * 주요 필드:
 * - seatId: PK, 자동 생성
 * - seatNum: 자리 번호
 * - seatsIp: 해당 자리의 컴퓨터 ip 번호
 * - x / y: 해당 자리의 x,y 좌표

 * 관계:
 */

@Entity
@Getter
@Setter
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @Column()
    private Long pcroomId;

    @Column(name = "seat_num", nullable = false)
    private Integer seatsNum;

    @Column(name = "seat_ip", nullable = false, length = 45)
    private String seatsIp;

    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpResult> ipResults;
}