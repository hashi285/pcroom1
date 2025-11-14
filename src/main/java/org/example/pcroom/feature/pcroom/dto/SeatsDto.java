package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Seat;

/**
 * PC방 정보

 * 주요 필드:
 * - zonNumber:
 */
@Getter
@Setter
public class SeatsDto {
    private String nameOfPcroom;
    private Integer seatNum;
    private String seatIp;
    private int x;
    private int y;

    public Seat toEntity(Pcroom pcroom) {
        Seat seat = new Seat();
        seat.setPcroomId(pcroom.getPcroomId());
        seat.setSeatsNum(this.seatNum);
        seat.setSeatsIp(this.seatIp);
        seat.setX(this.x);
        seat.setY(this.y);
        return seat;
    }

}

