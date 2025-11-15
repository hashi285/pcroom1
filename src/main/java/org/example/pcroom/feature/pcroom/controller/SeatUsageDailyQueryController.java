package org.example.pcroom.feature.pcroom.controller;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.entity.SeatDailyUsageWithInfoDto;
import org.example.pcroom.feature.pcroom.service.SeatUsageDailyQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pcroom/seat-usage-daily")
@RequiredArgsConstructor
public class SeatUsageDailyQueryController {

    private final SeatUsageDailyQueryService queryService;

    @GetMapping("/{pcroomId}/range-with-info")
    public ResponseEntity<List<SeatDailyUsageWithInfoDto>> getDailyUsageWithInfo(
            @PathVariable Long pcroomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<SeatDailyUsageWithInfoDto> result = queryService.getDailyUsageWithSeatInfo(pcroomId, startDate, endDate);
        return ResponseEntity.ok(result);
    }
}
