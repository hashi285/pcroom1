package org.example.pcroom.feature.pcroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pcroom.feature.pcroom.dto.IpResultDto;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.PingUtilizationDto;
import org.example.pcroom.feature.pcroom.dto.SeatUsageDailyResponse;
import org.example.pcroom.feature.pcroom.service.PcroomService;
import org.example.pcroom.feature.pcroom.service.SeatUsageService;
import org.example.pcroom.feature.pcroom.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("api/pcrooms")
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "앱 주요 기능 API", description = "모든 회원이 사용한다.")

public class PcroomController {
    private final PcroomService pcroomService;
    private final SeatUsageService seatUsageService;
    private final AiService aiService;

    @GetMapping("/{pcroomId}/utilization")
    @Operation(summary = "피시방 가동률 반환", description = "피시방 가동률을 'double' 형태로 반환한다.")
    public ResponseEntity<PingUtilizationDto> getPcroomStatus(@PathVariable Long pcroomId) throws Exception {
        return ResponseEntity.ok().body(pcroomService.getStatusFromCache(pcroomId));
    }

    @GetMapping
    @Operation(summary = "피시방 LIKE 검색", description = "검색 단어가 들어간 피시방을 반환한다.")
    public ResponseEntity<List<PcroomDto>> searchPcrooms(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(pcroomService.searchPcrooms(name));
    }

    @GetMapping("/사용률/{pcroomId}")
    @Operation(summary = "자리별 사용률을 반환한다.")
    public ResponseEntity<List<SeatUsageDailyResponse>> getSeatUsage(@PathVariable Long pcroomId, @RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseEntity.ok(seatUsageService.getDailyUsage(pcroomId, start, end));
    }

    @GetMapping("/pcroomInfo/{pcroomId}")
    @Operation(summary = "피시방의 기본 정보를 반환합니다.")
    public ResponseEntity<PcroomDto.PcroomInfo> getPcroomInfo(@PathVariable Long pcroomId) {
        return ResponseEntity.ok(pcroomService.getPcroomInfo(pcroomId));
    }

    @GetMapping("/seatInfo/{pcroomId}")
    @Operation(summary = "피시방 좌석의 정보를 반환합니다.")
    public ResponseEntity<List<PcroomDto.seatInfo>> getSeatInfo(@PathVariable Long pcroomId) {
        return ResponseEntity.ok(pcroomService.seatInfo(pcroomId));
    }

    @GetMapping("/{pcroomId}/seat")
    @Operation(summary = "피시방 좌석별 최신 상태 반환")
    public ResponseEntity<List<IpResultDto.SeatStatusDto>> getLatestSeats(@PathVariable Long pcroomId) throws Exception {
        return ResponseEntity.ok(pcroomService.getSeatStatusFromCache(pcroomId));
    }

    @GetMapping("/{pcroomId}/structures")
    @Operation(summary = "피시방 구조물 정보 반환")
    public ResponseEntity<List<org.example.pcroom.feature.pcroom.dto.StructureDto>> getStructures(@PathVariable Long pcroomId) {
        return ResponseEntity.ok(pcroomService.getStructures(pcroomId));
    }

    @PostMapping(value = "/auto-layout", consumes = "multipart/form-data")
    @Operation(summary = "AI 도면 자동 분석", description = "도면 이미지를 받아 AI 서버에 전달하고 좌표 배열(JSON)을 반환한다.")
    public ResponseEntity<String> autoLayout(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(aiService.analyzeBlueprint(file));
    }
}