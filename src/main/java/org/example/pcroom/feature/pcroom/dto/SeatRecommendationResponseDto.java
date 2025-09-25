package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SeatRecommendationResponseDto {
    private Long pcroomId;
    private List<SeatsDto> seats;



}
