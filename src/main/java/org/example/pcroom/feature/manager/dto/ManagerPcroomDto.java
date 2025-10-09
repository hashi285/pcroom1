package org.example.pcroom.feature.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
    public static class FindByManagerId{
        private Long pcroomId;

        private String nameOfPcroom;

        private double utilization;

        private LocalDateTime time;

    }

    @Getter
    @AllArgsConstructor
    public static class FindHourlyUtilization {
        private Long pcroomId;
        private String pcroomName;
        private double utilization;
        private LocalDateTime recordedAt;
    }

    @Getter
    @AllArgsConstructor
    public static class PcroomManager{
        private Long pcroomId;
        private String nameOfPcroom;
    }
}