package org.example.pcroom.feature.pcroom.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PcSeatsDto {
    private String seat_label;
    private String seat_number;
}
