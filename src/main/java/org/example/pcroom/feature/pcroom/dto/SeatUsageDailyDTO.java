package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SeatUsageDailyDTO {
    private Long seatId;
    private Long pcroomId;
    private Long totalUsedSeconds;
}