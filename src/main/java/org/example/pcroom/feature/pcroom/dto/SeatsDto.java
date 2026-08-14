package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.enums.SeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * PC방 정보
 * <p>
 * 주요 필드:
 * - zonNumber:
 */
@Getter
@Setter
public class SeatsDto {
    @NotBlank(message = "피시방 이름은 필수입니다.")
    private String nameOfPcroom;
    
    @Min(value = 1, message = "좌석 번호는 1 이상이어야 합니다.")
    private Integer seatNum;
    
    private String seatIp;
    
    @Min(value = 0, message = "X 좌표는 음수가 될 수 없습니다.")
    private int x;
    
    @Min(value = 0, message = "Y 좌표는 음수가 될 수 없습니다.")
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

