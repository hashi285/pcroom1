package org.example.pcroom.feature.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.admin.dto.PcroomListDto;
import org.example.pcroom.feature.admin.dto.UserListDto;
import org.example.pcroom.feature.admin.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


/**
 *
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 API", description = "관리자")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "유저 리스트 반환", description = "유저 리스트를 반환합니다")
    @GetMapping("/userList")
    public List<UserListDto> userList() {
        return adminService.getAllUsers();
    }

    @Operation(summary = "피시방 리스트 반환", description = "피시방 리스트를 반환합니다.")
    @GetMapping("/pcroomList")
    public List<PcroomListDto> pcroomList() {
        return adminService.getAllPcroom();
    }


}