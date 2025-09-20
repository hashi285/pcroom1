package org.example.pcroom.feature.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.user.dto.LoginRequest;
import org.example.pcroom.feature.user.dto.SignupRequest;
import org.example.pcroom.feature.user.dto.SignupResponse;
import org.example.pcroom.feature.user.service.UserService;
import org.example.pcroom.global.config.security.CustomUserDetails;
import org.example.pcroom.global.config.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 로그인 성공 후 UserDetails 에서 userId 추출
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long userId = userDetails.getUserId(); // DB에서 가져온 ID

            String jwt = jwtUtil.generateToken(userId, userDetails.getUsername());
            return ResponseEntity.ok(Map.of("token", jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: " + e.getMessage());
        }
    }

    /**
     * 즐겨찾기 추가
     */
    @PostMapping("/{pcroomId}")
    @Operation(summary = "즐겨찾기 추가")
    public ResponseEntity<Void> addFavorite(
            Authentication authentication, // Authentication 객체 직접 받기
            @PathVariable Long pcroomId) {

        // CustomUserDetails로 캐스팅
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        userService.addFavorite(userId, pcroomId);
        return ResponseEntity.ok().build();
    }


    /**
     * 즐겨찾기 삭제
     */
    @DeleteMapping("/{pcroomId}")
    @Operation(summary = "즐겨찾기 삭제")
    public ResponseEntity<Void> removeFavorite(
            Authentication authentication,
            @PathVariable Long pcroomId) {
        // CustomUserDetails로 캐스팅
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        System.out.println("--------------------------------------------"+ pcroomId);
        userService.removeFavorite(userId, pcroomId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 즐겨찾기한 PC방 이름 목록 조회
     */
    @GetMapping
    @Operation(summary = "즐겨찾기한 PC방 이름 목록 조회")
    public ResponseEntity<List<String>> getFavoritePcrooms(
            Authentication authentication) {
        // CustomUserDetails로 캐스팅
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        List<String> favorites = userService.isFavorite(userId);
        return ResponseEntity.ok(favorites);
    }

}
