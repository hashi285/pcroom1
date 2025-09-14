package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * ip 검색 결과

 * 주요 필드:
 * - resultId: PK, 자동 생성
 * - pcroomId: pcroomId FK
 * - seatId: seatId FK
 * - result: 컴퓨터 생준 확인 결과 (ture / false)
 * - time: 컴퓨터 생존 확인 시간
 */

@Entity
@Getter
@Setter
@Table(name = "ip_result")
public class IpResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false)
    private Boolean result;

    @Column(nullable = false)
    private Long pcroomId;

    @Column(nullable = false)
    private Long utilizationId;
}