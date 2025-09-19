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
import java.util.concurrent.ExecutionException;

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

    /**
     * 피시방 좌석 저장
     * @param seatsDtos
     * @return
     */
    @PostMapping("/seats")
    @Operation(summary = "피시방 좌석 저장")
    public ResponseEntity<List<SeatsDto>> registerSeats(@RequestBody List<SeatsDto> seatsDtos) {

        List<SeatsDto> savedSeats = pcRoomService.registerNewSeat(seatsDtos);
        return ResponseEntity.ok(savedSeats);
    }

    /**
     * 피시방 검색
     * @param name
     * @return
     */
    @GetMapping("/search")
    @Operation(summary = "피시방 LIKE 검색", description = "검색 단어가 들어간 피시방을 반환한다.")
    public ResponseEntity<List<PcroomDto>> searchPcrooms(@RequestParam String name){
        List<PcroomDto> result = pcRoomService.searchPcrooms(name);
        return ResponseEntity.ok(result);
    }
}