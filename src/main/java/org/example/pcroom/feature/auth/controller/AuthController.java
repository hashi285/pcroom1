package org.example.pcroom.feature.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.auth.dto.LoginRequest;
import org.example.pcroom.feature.auth.dto.SingupRequset;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pc")
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok("로그인 성공");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> singup(@RequestBody SingupRequset requset){
        return ResponseEntity.ok("회원가입 성공");
    }
}
