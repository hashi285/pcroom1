package org.example.pcroom.feature.user.service;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.user.dto.SignupRequest;
import org.example.pcroom.feature.user.dto.SignupResponse;
import org.example.pcroom.feature.user.entity.User;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.example.pcroom.global.config.security.JwtUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Lazy
    private final AuthenticationManager authenticationManager;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateDate(LocalDateTime.now());
        user.setNickname(request.getNickname());
        userRepository.save(user);

        return new SignupResponse(user.getEmail(), "회원가입 성공");
    }

}
