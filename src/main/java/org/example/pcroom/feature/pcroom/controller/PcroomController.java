package org.example.pcroom.feature.pcroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.IpResultDto;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.PingUtilizationDto;
import org.example.pcroom.feature.pcroom.dto.SeatUsageDailyResponse;
import org.example.pcroom.feature.pcroom.service.PcroomService;
import org.example.pcroom.feature.pcroom.service.SeatUsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RequestMapping("api/pcrooms")
@RestController
@RequiredArgsConstructor
@Tag(name = "앱 주요 기능 API", description = "이 앱의 주요 기능이 모여있는 API 입니다. 모든 회원이 사용합니다.")

public class PcroomController {
    private final PcroomService pcroomService;
    private final SeatUsageService seatUsageService;

    /** 완료
     *
     * @param pcroomId
     * @return
     * @throws Exception
     */
    @GetMapping("/{pcroomId}/utilization")
    @Operation(summary = "피시방 가동률 확인", description = "피시방 가동률 반환")
    public PingUtilizationDto getSeats(@PathVariable Long pcroomId) throws Exception {
        return pcroomService.canUseSeat(pcroomId);
    }


    /**
     * 피시방 검색
     * @param name
     * @return
     */
    @GetMapping
    @Operation(summary = "피시방 LIKE 검색", description = "검색 단어가 들어간 피시방을 반환한다.")
    public ResponseEntity<List<PcroomDto>> searchPcrooms(@RequestParam(required = false) String name){
        List<PcroomDto> result = pcroomService.searchPcrooms(name);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/사용률/{pcroomId}")
    @Operation(summary = "자리별 사용률을 반환한다.")
    public ResponseEntity<List<SeatUsageDailyResponse>> getSeatUsage(
            @PathVariable Long pcroomId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        List<SeatUsageDailyResponse> usageList = seatUsageService.getDailyUsage(pcroomId, start, end);
        return ResponseEntity.ok(usageList);
    }

    @GetMapping("/pcroomInfo/{pcroomId}")
    @Operation(summary = "피시방의 기본 정보를 반환합니다.")
    public ResponseEntity<PcroomDto.PcroomInfo> getPcroomInfo(@PathVariable Long pcroomId) {
        PcroomDto.PcroomInfo pcroomInfo = pcroomService.getPcroomInfo(pcroomId);
        return ResponseEntity.ok(pcroomInfo);
    }

    @GetMapping("/seatInfo/{pcroomId}")
    @Operation(summary = "피시방 좌석의 정보를 반환합니다.")
    public ResponseEntity<List<PcroomDto.seatInfo>> getSeatInfo(@PathVariable Long pcroomId) {
        List<PcroomDto.seatInfo> seatInfoList  = pcroomService.seatInfo(pcroomId);
        return ResponseEntity.ok(seatInfoList);
    }

    @GetMapping("/{pcroomId}/seat")
    @Operation(summary = "피시방 좌석별 최신 상태 반환")
    public ResponseEntity<List<IpResultDto.SeatStatusDto>> getLatestSeats(@PathVariable Long pcroomId) throws ExecutionException, InterruptedException {
        List<IpResultDto.SeatStatusDto> latestSeats = pcroomService.getLatestSeatResults(pcroomId);
        return ResponseEntity.ok(latestSeats);
    }


}