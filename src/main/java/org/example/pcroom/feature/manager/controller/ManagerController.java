package org.example.pcroom.feature.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.manager.dto.ManagerPcroomDto;
import org.example.pcroom.feature.manager.service.ManagerService;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.SeatsDto;
import org.example.pcroom.feature.pcroom.service.PcroomService;
import org.example.pcroom.global.config.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pcrooms")
@RequiredArgsConstructor
@Tag(name = "관리자 + 피시방 매니저 API", description = "앱 관리자 및 피시방 매니저만 사용합니다.")
public class ManagerController {

    private final PcroomService pcRoomService;
    private final ManagerService managerService;
    /**
     * 피시방 저장
     * @param request
     * @return
     */
    @PostMapping
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
}
