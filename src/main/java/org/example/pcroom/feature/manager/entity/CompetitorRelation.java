package org.example.pcroom.feature.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 경쟁 피시방 테이블
 */
@Entity
@Getter
@Setter
@Table(name = "pcrooms_competitors")
public class CompetitorRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long pcroomId;
}
