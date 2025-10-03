package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.hibernate.id.IncrementGenerator;

@Getter
@Setter
@AllArgsConstructor
public class PcroomDto {

    private String nameOfPcroom;
    private Long pcroomId;

    @Getter
    @AllArgsConstructor
    public static class ReadPcRoomResponse{
        private Long pcroomId;

        private String nameOfPcroom;

        private Integer seatCount;

        private int port;

        private int width;

        private int height;
    }

    @Getter
    @RequiredArgsConstructor
    public static class CreatePcRoomRequest{

        private final String nameOfPcroom;

        private final Integer seatCount;

        private final int port;

        private final int width;

        private final int height;
    }


    public static PcroomDto fromEntity(Pcroom pcroom) {
        return new PcroomDto(
                pcroom.getNameOfPcroom(),
                pcroom.getPcroomId()
     ); }
}