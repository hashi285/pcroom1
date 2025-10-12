package org.example.pcroom.feature.pcroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class SeatUsageDailyResponse {
    private Long seatId;
    private Long pcroomId;
    private LocalDate date;
    private double usedPercent; // 자리별 사용률
}