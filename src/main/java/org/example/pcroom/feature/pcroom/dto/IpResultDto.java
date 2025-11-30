package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
public class IpResultDto {

    @Getter
    @NoArgsConstructor  // Jackson 역직렬화용
    @AllArgsConstructor
    public static class SeatStatusDto {
        private Integer seatsNum;
        private Boolean result;
    }

}

