package org.example.pcroom.feature.admin.controller;

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
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/userList")
    public List<UserListDto> userList() {
        return adminService.getAllUsers();
    }

    @GetMapping("/pcroomList")
    public List<PcroomListDto> pcroomList() {
        return adminService.getAllPcroom();
    }
}