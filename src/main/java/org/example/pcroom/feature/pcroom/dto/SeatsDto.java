package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.enums.SeatType;

/**
 * PC방 정보
 * <p>
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
    private String seatType = "NORMAL";

    public Seat toEntity(Pcroom pcroom) {
        Seat seat = new Seat();
        seat.setPcroomId(pcroom.getPcroomId());
        seat.setSeatsNum(this.seatNum);
        seat.setSeatsIp(this.seatIp);
        seat.setX(this.x);
        seat.setY(this.y);
        if (this.seatType != null) {
            seat.setSeatType(SeatType.valueOf(this.seatType));
        } else {
            seat.setSeatType(SeatType.NORMAL);
        }
        return seat;
    }
}

