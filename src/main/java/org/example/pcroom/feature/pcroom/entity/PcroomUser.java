package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User-Pcroom 사이의 다대다 매핑을 위한 중간 테이블
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PcroomUser {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private  Long userId;

    @Column(nullable = false)
    private Long pcroomId;
}
