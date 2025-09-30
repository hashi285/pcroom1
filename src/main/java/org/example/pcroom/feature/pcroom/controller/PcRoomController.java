package org.example.pcroom.feature.pcroom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.PingUtilizationDto;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.service.PcroomService;
import org.example.pcroom.global.config.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RequestMapping("pcrooms")
@RestController
@RequiredArgsConstructor
@Tag(name = "앱 주요 기능 API", description = "이 앱의 주요 기능이 모여있는 API 입니다. 모든 회원이 사용합니다.")

public class PcRoomController {
    private final PcroomService pcRoomService;

    /** 완료
     *
     * @param pcroomId
     * @return
     * @throws Exception
     */
    @GetMapping("/{pcroomId}/utilization")
    @Operation(summary = "피시방 가동률 확인", description = "피시방 가동률 반환")
    public PingUtilizationDto getSeats(@PathVariable Long pcroomId) throws Exception {
        return pcRoomService.canUseSeat(pcroomId);
    }


    /**
     * 피시방 검색
     * @param name
     * @return
     */
    @GetMapping
    @Operation(summary = "피시방 LIKE 검색", description = "검색 단어가 들어간 피시방을 반환한다.")
    public ResponseEntity<List<PcroomDto>> searchPcrooms(@RequestParam(required = false) String name){
        List<PcroomDto> result = pcRoomService.searchPcrooms(name);
        return ResponseEntity.ok(result);
    }


    // 피시방 자리 추천 알고리즘
//    @PostMapping("/recommendation")
//    @Operation(summary = "자리 추천", description = "사용 가능한 자리를 추천한다.")
//    public List<Pcroom> recommendation (Authentication authentication,
//                                        @RequestBody Integer partySize){
//
//        // CustomUserDetails로 캐스팅
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Long userId = userDetails.getUserId();
//
//        try {
//            return pcRoomService.recommendation(partySize,userId);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//    }
}