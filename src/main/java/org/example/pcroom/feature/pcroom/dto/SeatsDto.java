package org.example.pcroom.feature.pcroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PC방 정보

 * 주요 필드:
 * - zonNumber:
 */

@Getter
@AllArgsConstructor
public class SeatsDto {
    private String nameOfPcroom;
    private Integer seatsNum;
    private String seatsIp;
    private int x;
    private int y;
}