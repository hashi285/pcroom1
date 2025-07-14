package org.example.pcroom.feature.pcroom.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class PcSeatsDto {
    private String seat_label;
    private String seat_number;
}
