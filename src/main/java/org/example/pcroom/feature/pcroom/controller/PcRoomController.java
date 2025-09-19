package org.example.pcroom.feature.pcroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.PingUtilizationDto;
import org.example.pcroom.feature.pcroom.dto.SeatsDto;
import org.example.pcroom.feature.pcroom.service.PcRoomService;
import org.example.pcroom.feature.pcroom.service.PingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("pc")
@RestController
@RequiredArgsConstructor
public class PcRoomController {
    private final PcRoomService pcRoomService;
    private final PingService pingService;

    /** 완료
     *
     * @param pcroomId
     * @return
     * @throws Exception
     */
    @PostMapping("/Utilization")
    @Operation(summary = "피시방 가동률 확인", description = "피시방 가동률 반환")
    public PingUtilizationDto getSeats(@RequestBody Long pcroomId) throws Exception {
        return pcRoomService.canUseSeat(pcroomId);
    }

    /**
     * 피시방 저장
     * @param request
     * @return
     */
    @PostMapping("/set_pcroom")
    @Operation(summary = "피시방 저장")
    public PcroomDto.ReadPcRoomResponse setPcroom(@RequestBody PcroomDto.CreatePcRoomRequest request) {

        return pcRoomService.registerNewPcroom(request);
    }

    @PostMapping("/seats")
    @Operation(summary = "피시방 좌석 저장")
    public ResponseEntity<List<SeatsDto>> registerSeats(@RequestBody List<SeatsDto> seatsDtos) {

        List<SeatsDto> savedSeats = pcRoomService.registerNewSeat(seatsDtos);
        return ResponseEntity.ok(savedSeats);
    }
}