package org.example.pcroom.feature.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ManagerPcroomDto {

    @Getter
    @AllArgsConstructor
    public static class addPcroomResponse{
        private Long userId;

        private Long pcroomId;
    }

    @Getter
    @AllArgsConstructor
    public static class removeManagerFromPcroom{
        private Long userId;

        private Long pcroomId;
    }

    @Getter
    @AllArgsConstructor
    public static class findByManagerId{
        private Long userId;

        private Long pcroomId;

        private String nameOfPcroom;


    }
}