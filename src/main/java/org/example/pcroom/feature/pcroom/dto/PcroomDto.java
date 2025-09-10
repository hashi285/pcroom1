package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class PcroomDto {

    @Getter
    @AllArgsConstructor
    public static class ReadPcRoomResponse{
        private Long pcroomId;

        private Long userId;

        private String nameOfPcroom;

        private int port;

        private int width;

        private int height;
    }

    @Getter
    @RequiredArgsConstructor
    public static class CreatePcRoomRequest{

        private final String nameOfPcroom;

        private final int port;

        private final int width;

        private final int height;
    }

}