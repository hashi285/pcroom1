package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.pcroom.feature.user.entity.User;

import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "pcroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;

    @OneToMany(mappedBy = "pcroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpResult> ipResults;
}