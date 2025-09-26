package org.example.pcroom.feature.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.user.service.UserService;
import org.example.pcroom.global.config.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
@Tag(name = "즐겨찾기 API", description = "회원이 즐겨찾기 기능을 사용합니다.")
public class FavoriteController {

    private final UserService userService;

    @PostMapping("/{pcroomId}")
    @Operation(summary = "즐겨찾기 추가")
    public ResponseEntity<Void> addFavorite(Authentication authentication, @PathVariable Long pcroomId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userService.addFavorite(userDetails.getUserId(), pcroomId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{pcroomId}")
    @Operation(summary = "즐겨찾기 삭제")
    public ResponseEntity<Void> removeFavorite(Authentication authentication, @PathVariable Long pcroomId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userService.removeFavorite(userDetails.getUserId(), pcroomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "즐겨찾기 목록 조회")
    public ResponseEntity<List<String>> getFavoritePcrooms(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> favorites = userService.isFavorite(userDetails.getUserId());
        return ResponseEntity.ok(favorites);
    }
}
