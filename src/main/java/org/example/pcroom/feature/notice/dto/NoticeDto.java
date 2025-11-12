package org.example.pcroom.feature.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NoticeDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticeTitleDto {
        private Long id;
        private String title;
        private LocalDateTime creationDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticeDetailDto {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime creationDate;
    }
}
