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
@RequestMapping("api/manager")
@RequiredArgsConstructor

public class ManagerController {

    private final PcroomService pcRoomService;
    private final ManagerService managerService;

    @Tag(name = "피시방 매니저 API (피시방 등록)", description = "매니저가 운영중인 피시방을 등록합니다.")
    @PostMapping("/pcrooms")
    @Operation(summary = "피시방 등록", description = "피시방을 등록합니다.")
    public ResponseEntity<PcroomDto.ReadPcRoomResponse> createPcroom(@RequestBody PcroomDto.CreatePcRoomRequest request) {
        return ResponseEntity.ok(pcRoomService.registerNewPcroom(request));
    }

    @Tag(name = "피시방 매니저 API (피시방 등록)", description = "매니저가 운영중인 피시방을 등록합니다.")
    @PostMapping("/pcrooms/seats")
    @Operation(summary = "피시방 좌석 등록", description = "피시방 등록 이후 좌석을 등록합니다.")
    public ResponseEntity<List<SeatsDto>> registerSeats(@RequestBody List<SeatsDto> seatsDtos) {
        return ResponseEntity.ok(pcRoomService.registerNewSeat(seatsDtos));
    }

    @Tag(name = "피시방 매니저 API", description = "피시방 매니저 및 관리자 전용 API")
    @GetMapping("/pcrooms")
    @Operation(summary = "자신이 운영중인 피시방 조회", description = "사용자가 운영중인 피시방을 조회합니다.")
    public ResponseEntity<List<ManagerPcroomDto.PcroomManager>> getManagerPcrooms(Authentication authentication) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(managerService.getManagerPcroom(userId));
    }

    @Tag(name = "피시방 매니저 API", description = "피시방 매니저 및 관리자 전용 API")
    @PostMapping("/pcrooms/{pcroomId}")
    @Operation(summary = "자신이 운영중인 피시방 등록", description = "사용자가 운영중인 피시방을 등록합니다.")
    public ResponseEntity<Void> registerManagerForPcroom(Authentication authentication, @PathVariable Long pcroomId) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        managerService.setManagerPcroom(userId, pcroomId);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "피시방 매니저 API", description = "피시방 매니저 및 관리자 전용 API")
    @DeleteMapping("/pcrooms/{pcroomId}")
    @Operation(summary = "자신이 운영중인 피시방 삭제", description = "사용자가 운영중인 피시방을 삭제합니다.")
    public ResponseEntity<Void> deleteManagerPcroom(Authentication authentication, @PathVariable Long pcroomId) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        managerService.deleteManagerFromPcroom(userId, pcroomId);
        return ResponseEntity.ok().build();
    }
}
