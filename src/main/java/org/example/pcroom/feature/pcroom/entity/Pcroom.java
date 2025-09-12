package org.example.pcroom.feature.pcroom.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

/**
 * PC방 정보
 * <p>
 * 주요 필드:
 * - pcroomId: PK, 자동 생성
 * - userId: userId FK
 * - nameOfPcroom: PC방 이름
 * - port: 서버 포트 번호
 * - width / height: 좌석 배치 크기
 */

@Entity
@Table(name = "pcrooms")
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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


    public static Pcroom register(
        Long userId, String nameOfPcroom, int port, int width, int height) {

        Objects.requireNonNull(userId, "null 넣지마라 개쉐이야");
        Objects.requireNonNull(nameOfPcroom, "null 넣지마라 개쉐이야");


        return Pcroom.builder()
            .userId(userId)
            .nameOfPcroom(nameOfPcroom)
            .port(port)
            .width(width)
            .height(height)
            .build();
    }

}