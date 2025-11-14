package org.example.pcroom.feature.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.notice.dto.NoticeDto;
import org.example.pcroom.feature.notice.service.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/notices")
@RequiredArgsConstructor
@Tag(name = "앱 공지사항", description = "이용자들에게 보여지는 피시방 git 관련 공지사항입니다.")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/{pcroomId}")
    @Operation(summary = "피시방 공지사항 목록 조회")
    public ResponseEntity<List<NoticeDto.NoticeTitleDto>> getNotices(@PathVariable Long pcroomId) {
        List<NoticeDto.NoticeTitleDto> noticeTitleDto = noticeService.findNotice(pcroomId);
        return ResponseEntity.ok(noticeTitleDto);
    }

    @GetMapping("/notice/{noticeId}")
    @Operation(summary = "공지사항 상세정보 조회")
    public ResponseEntity<NoticeDto.NoticeDetailDto> getNotice( @PathVariable Long noticeId) {
        NoticeDto.NoticeDetailDto noticeDetailDto = noticeService.findNoticeDetail(noticeId);
        return ResponseEntity.ok(noticeDetailDto);
    }

}
