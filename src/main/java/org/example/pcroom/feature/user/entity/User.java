package org.example.pcroom.feature.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pcroom.feature.user.enums.UserRole;

import java.time.LocalDateTime;

/**
 * 유저 정보
 * <p>
 * 주요 필드:
 * - userId: PK, 자동 생성
 * - email: 이메일
 * - nickname: 사용자 이름
 * - password: 인코딩 비밀번호
 * - createDate: 계정 생성 날짜 / 시간
 * - role: 사용자 role(ADMIN, OWNER, USER)
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(length = 15)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserRole role;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }
}