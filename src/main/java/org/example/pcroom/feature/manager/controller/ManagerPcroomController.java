package org.example.pcroom.feature.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.manager.dto.ManagerPcroomDto;
import org.example.pcroom.feature.manager.service.ManagerService;
import org.example.pcroom.feature.pcroom.service.PcroomService;
import org.example.pcroom.global.config.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pcrooms")
@RequiredArgsConstructor
@Tag(name = "매니저-피시방 매핑 API", description = "피시방 매니저가 경쟁 피시방을 등록/삭제합니다.")
public class ManagerPcroomController {

    private final PcroomService pcRoomService;
    private final ManagerService managerService;

    @PostMapping("/{pcroomId}/managers")
    @Operation(summary = "경쟁 피시방 등록", description = "가동률을 확인하고 싶은 경쟁 피시방을 등록합니다.")
    public ResponseEntity<ManagerPcroomDto.addPcroomResponse> assignManager(Authentication authentication, @PathVariable Long pcroomId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        ManagerPcroomDto.addPcroomResponse response = managerService.assignManagerToPcroom(userDetails.getUserId(), pcroomId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{pcroomId}/managers")
    @Operation(summary = "경쟁 피시방 삭제", description = "등록되어 있던 경쟁 피시방을 삭제합니다.")
    public ResponseEntity<Void> removeManager (Authentication authentication, @PathVariable Long pcroomId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        managerService.removeManagerFromPcroom(userDetails.getUserId(), pcroomId);
        return ResponseEntity.noContent().build();
    }
}
