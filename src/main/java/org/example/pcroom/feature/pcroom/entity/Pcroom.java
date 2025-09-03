package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.pcroom.feature.user.entity.User;
import java.util.List;

/**
 * PC방 정보

 * 주요 필드:
 * - pcroomId: PK, 자동 생성
 * - nameOfPcroom: PC방 이름
 * - port: 서버 포트 번호
 * - width / height: 좌석 배치 크기

 * 관계:
 */

@Entity
@Getter
@Setter
@Table(name = "pcrooms")
public class Pcroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pcroomId;

    @Column(nullable = false, length = 50)
    private String nameOfPcroom;

    @Column(nullable = false)
    private int port;

    @Column(nullable = false)
    private int width;

    @Column(nullable = false)
    private int height;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "pcroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;

    @OneToMany(mappedBy = "pcroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpResult> ipResults;
}