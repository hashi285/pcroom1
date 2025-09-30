package org.example.pcroom.feature.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class FavoriteDto {

    @Getter
    @AllArgsConstructor
    public static class FavoriteResponse {
        private Long pcroomId;
        private String nameOfPcroom;
    }
}
