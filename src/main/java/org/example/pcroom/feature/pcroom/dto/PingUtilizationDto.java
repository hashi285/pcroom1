package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PingUtilizationDto {
    private Long pcroomId;
    private String nameOfPcroom;
    private double utilization;
    private Integer seatCount;
    private Integer usedSeatCount;
}
