package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * PC방 정보

 * 주요 필드:
 * - pcroomId: PK, 자동 생성
 * - userId: userId FK
 * - nameOfPcroom: PC방 이름
 * - port: 서버 포트 번호
 * - width / height: 좌석 배치 크기
 */

@Entity
@Getter
@Setter
@Table(name = "pcrooms")
public class Pcroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pcroomId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String nameOfPcroom;

    @Column(nullable = false)
    private int port;

    @Column(nullable = false)
    private int width;

    @Column(nullable = false)
    private int height;
}