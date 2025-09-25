package org.example.pcroom.feature.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PcroomListDto {

    private Long pcroomId;
    private String nameOfPcroom;
    private Integer seatCount;
}
