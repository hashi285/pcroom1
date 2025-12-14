package org.example.pcroom.feature.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 피시방 사장이 운영중인 피시방과 매핑이 됩니다.
 */
@Entity
@Getter
@Setter
@Table(name = "pcrooms_managers")
public class PcroomManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long pcroomId;
}