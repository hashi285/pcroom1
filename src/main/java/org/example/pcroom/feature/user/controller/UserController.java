package org.example.pcroom.feature.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.user.dto.SignupRequest;
import org.example.pcroom.feature.user.dto.SignupResponse;
import org.example.pcroom.feature.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }
}
