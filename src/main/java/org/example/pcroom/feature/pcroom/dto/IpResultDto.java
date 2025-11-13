package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class IpResultDto {
    @Getter
    @RequiredArgsConstructor
    public static class SeatStatusDto {
        private final Integer seatsNum;
        private final Boolean result;
    }
}