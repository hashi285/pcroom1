package org.example.pcroom.feature.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.manager.dto.ManagerPcroomDto;
import org.example.pcroom.feature.manager.entity.PcroomManager;
import org.example.pcroom.feature.manager.service.ManagerService;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.SeatsDto;
import org.example.pcroom.feature.pcroom.service.PcroomService;
import org.example.pcroom.global.config.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
@Tag(name = "피시방 관리자 API", description = "피시방 매니저 및 관리자 전용 API")
public class ManagerController {

    private final PcroomService pcRoomService;
    private final ManagerService managerService;

    @PostMapping("/pcrooms")
    @Operation(summary = "피시방 등록", description = "피시방을 등록합니다.")
    public ResponseEntity<PcroomDto.ReadPcRoomResponse> createPcroom(@RequestBody PcroomDto.CreatePcRoomRequest request) {
        return ResponseEntity.ok(pcRoomService.registerNewPcroom(request));
    }

    @PostMapping("/pcrooms/seats")
    @Operation(summary = "피시방 좌석 등록", description = "피시방 좌석을 등록합니다.")
    public ResponseEntity<List<SeatsDto>> registerSeats(@RequestBody List<SeatsDto> seatsDtos) {
        return ResponseEntity.ok(pcRoomService.registerNewSeat(seatsDtos));
    }

    @GetMapping("/pcrooms")
    @Operation(summary = "내가 운영 중인 피시방 목록 조회", description = "운영중인 피시방 목록을 조회합니다.")
    public ResponseEntity<List<ManagerPcroomDto.PcroomManager>> getManagerPcrooms(Authentication authentication) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(managerService.getManagerPcroom(userId));
    }

    @PostMapping("/pcrooms/{pcroomId}")
    @Operation(summary = "피시방-매니저 관계 등록", description = "운영중인 피시방을 등록합니다.")
    public ResponseEntity<Void> registerManagerForPcroom(Authentication authentication, @PathVariable Long pcroomId) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        managerService.setManagerPcroom(userId, pcroomId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/pcrooms/{pcroomId}")
    @Operation(summary = "피시방-매니저 관계 삭제", description = "내 피시방을 삭제합니다.")
    public ResponseEntity<Void> deleteManagerPcroom(Authentication authentication, @PathVariable Long pcroomId) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        managerService.deleteManagerFromPcroom(userId, pcroomId);
        return ResponseEntity.ok().build();
    }
}
