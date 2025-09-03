package org.example.pcroom.feature.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 유저 정보

 * 주요 필드:
 * - userId: PK, 자동 생성
 * - email: 이메일
 * - nickname: 사용자 이름
 * - password: 인코딩 비밀번호
 * - createDate: 계정 생성 날짜 / 시간
 * - role: 사용자 role(ADMIN, OWNER, USER)

 * 관계:
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

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 15)
    private String nickname;

    @Column()
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column()
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pcroom> pcrooms;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }
}