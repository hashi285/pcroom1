package org.example.pcroom.feature.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.admin.dto.PcroomListDto;
import org.example.pcroom.feature.admin.dto.UserListDto;
import org.example.pcroom.feature.admin.service.AdminService;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 *
 */
@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 API", description = "앱 관리자만 사용합니다.")
public class AdminController {
    private final AdminService adminService;

    /**
     * 유저 리스트를 반환한다.
     *
     * @return 유저 리스트 반환
     */
    @Operation(summary = "유저 리스트 반환", description = "유저 리스트를 반환합니다")
    @GetMapping("/users")
    public List<UserListDto> userList() {
        return adminService.getAllUsers();
    }

    /**
     * 피시방 리스트를 반환한다.
     *
     * @return 피시방 리스트 반환
     */
    @Operation(summary = "피시방 리스트 반환", description = "피시방 리스트를 반환합니다.")
    @GetMapping("/pcrooms")
    public List<PcroomListDto> pcroomList() {
        return adminService.getAllPcroom();
    }

    /**
     * 등록된 피시방 삭제한다.
     *
     * @param pcroomId 피시방 삭제
     */
    @Operation(summary = "등록된 피시방 삭제", description = "등록된 피시방을 삭제합니다.")
    @DeleteMapping("/pcrooms/{pcroomId}")
    public void deletePcroom(@PathVariable Long pcroomId) {
        adminService.removePcroom(pcroomId);
    }



}