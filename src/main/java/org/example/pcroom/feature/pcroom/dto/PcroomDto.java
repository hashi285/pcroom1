package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.enums.SeatType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class PcroomDto {

    private String nameOfPcroom;
    private Long pcroomId;

    @Getter
    @AllArgsConstructor
    public static class ReadPcRoomResponse {

        private Long pcroomId;
        private String nameOfPcroom;
        private Integer seatCount;
        private int port;
        private int width;
        private int height;
    }

    @Getter
    @RequiredArgsConstructor
    public static class CreatePcRoomRequest {

        @NotBlank(message = "피시방 이름은 필수입니다.")
        @Size(min = 1, max = 50, message = "피시방 이름은 1자 이상 50자 이하여야 합니다.")
        private final String nameOfPcroom;

        @NotNull(message = "좌석 수는 필수입니다.")
        @Min(value = 1, message = "좌석 수는 최소 1개 이상이어야 합니다.")
        @Max(value = 1000, message = "좌석 수는 최대 1000개 이하여야 합니다.")
        private final Integer seatCount;

        @Min(value = 1, message = "유효하지 않은 포트 번호입니다.")
        @Max(value = 65535, message = "유효하지 않은 포트 번호입니다.")
        private final int port;

        @Min(value = 100, message = "가로 길이는 최소 100 이상이어야 합니다.")
        @Max(value = 10000, message = "가로 길이는 최대 10000 이하여야 합니다.")
        private final int width;

        @Min(value = 100, message = "세로 길이는 최소 100 이상이어야 합니다.")
        @Max(value = 10000, message = "세로 길이는 최대 10000 이하여야 합니다.")
        private final int height;
    }

    @Getter
    @RequiredArgsConstructor
    public static class PcroomInfo {
        private final String nameOfPcroom;
        private final int width;
        private final int height;
    }

    @Getter
    @RequiredArgsConstructor
    public static class seatInfo {
        private final Long pcroomId;
        private final Integer seatsNum;
        private final int x;
        private final int y;
        private final SeatType seatType;
    }

    @Getter
    @RequiredArgsConstructor
    public static class SeatStatusDto {
        private final Long seatId;
        private final Boolean result;
    }

    public static PcroomDto fromEntity(Pcroom pcroom) {
        return new PcroomDto(
                pcroom.getNameOfPcroom(),
                pcroom.getPcroomId()
        );
    }
}