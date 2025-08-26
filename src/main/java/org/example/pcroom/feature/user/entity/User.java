package org.example.pcroom.feature.user.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.pcroom.feature.pcroom.entity.Pcroom;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.List;

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