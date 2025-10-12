package org.example.pcroom.feature.manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.manager.dto.ManagerPcroomDto;
import org.example.pcroom.feature.manager.repository.CompetitorRelationRepository;
import org.example.pcroom.feature.manager.service.ManagerService;
import org.example.pcroom.feature.pcroom.service.PcroomService;
import org.example.pcroom.global.config.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("manager-favorites")
@RequiredArgsConstructor
@Tag(name = "피시방 매니저 API(경쟁 피시방 등록 관련)", description = "피시방 매니저가 경쟁 피시방을 등록/조회/삭제합니다.")
public class ManagerPcroomController {

    private final ManagerService managerService;
    private final CompetitorRelationRepository competitorRelationRepository;

    @PostMapping("/{pcroomId}")
    @Operation(summary = "경쟁 피시방 등록", description = "가동률을 확인하고 싶은 경쟁 피시방을 등록합니다.")
    public ResponseEntity<ManagerPcroomDto.addPcroomResponse> assignManager(Authentication authentication, @PathVariable Long pcroomId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        ManagerPcroomDto.addPcroomResponse response = managerService.assignManagerToPcroom(userDetails.getUserId(), pcroomId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pcroomId}/favorite")
    @Operation(summary = "경쟁 피시방 조회", description = "경쟁 피시방을 조회합니다.")
    public ResponseEntity<?> showManager (Authentication authentication, @PathVariable Long pcroomId) {
        return null;
        }

    @DeleteMapping("/{pcroomId}")
    @Operation(summary = "경쟁 피시방 삭제", description = "경쟁 피시방을 삭제합니다.")
    public ResponseEntity<Void> removeManager (Authentication authentication, @PathVariable Long pcroomId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        managerService.removeManagerFromPcroom(userDetails.getUserId(), pcroomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "1시간 단위 가동률 조회", description = "등록한 피시방들의 1시간 단위 가동률을 조회합니다. hours 파라미터로 최근 n시간 선택 가능")
    public ResponseEntity<List<ManagerPcroomDto.FindHourlyUtilization>> getHourlyUtilization(
            Authentication authentication,
            @RequestParam(defaultValue = "24") int hours // 기본 최근 24시간
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        List<Long> pcroomIds = competitorRelationRepository.findPcroomIdByUserId(userDetails.getUserId());

        List<ManagerPcroomDto.FindHourlyUtilization> result = managerService.getHourlyUtilization(pcroomIds, hours);

        return ResponseEntity.ok(result);
    }
}
