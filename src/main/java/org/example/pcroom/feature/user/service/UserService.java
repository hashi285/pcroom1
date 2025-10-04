package org.example.pcroom.feature.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.user.enums.UserRole;
import org.example.pcroom.feature.user.dto.FavoriteDto;
import org.example.pcroom.feature.user.dto.SignupRequest;
import org.example.pcroom.feature.user.dto.SignupResponse;
import org.example.pcroom.feature.user.entity.Favorite;
import org.example.pcroom.feature.user.entity.User;
import org.example.pcroom.feature.user.repository.FavoriteRepository;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.example.pcroom.global.config.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FavoriteRepository favoriteRepository;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateDate(LocalDateTime.now());
        user.setNickname(request.getNickname());
        user.setRole(UserRole.USER);
        userRepository.save(user);

        return new SignupResponse(user.getEmail(), "회원가입 성공");
    }

    public UserRole userRole(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        return user.getRole();
    }

    /**
     * 즐겨찾기 추가
     * @param userId
     * @param pcroomId
     */
    @Transactional
    public void addFavorite(Long userId, Long pcroomId) {
        if (!favoriteRepository.existsByUserIdAndPcroomId(userId, pcroomId)) {
            favoriteRepository.save(new Favorite(userId, pcroomId));
        }
    }

    /**
     * 즐겨찾기 삭제
     * @param userId
     * @param itemId
     */
    @Transactional
    public void removeFavorite(Long userId, Long itemId) {
        favoriteRepository.deleteByUserIdAndPcroomId(userId, itemId);
    }

    /**
     * 즐겨찾기한 피시방 조회
     * @param userId
     * @return
     */
    @Transactional
    public List<FavoriteDto> isFavorite(Long userId) {
        return favoriteRepository.findFavoritePcroomsByUserId(userId);
    }
}
