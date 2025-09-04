package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * ip 검색 결과

 * 주요 필드:
 * - resultId: PK, 자동 생성
 * - result: 컴퓨터 생준 확인 결과 (ture / false)
 * - time: 컴퓨터 생존 확인 시간

 * 관계:
 */

@Entity
@Getter
@Setter
@Table(name = "ip_result")
public class IpResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @Column()
    private String seatId;

    @Column(nullable = false)
    private Boolean result;

    @Column(nullable = false, updatable = false)
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pcroom_id", nullable = false)
    private Pcroom pcroom;


    @PrePersist
    protected void onCreate() {
        this.time = LocalDateTime.now();
    }
}