package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class IpResultDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatStatusDto {
        private Integer seatsNum;
        private Boolean result;
    }
}

