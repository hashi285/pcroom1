package org.example.pcroom.feature.user.service;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.auth.dto.SingupRequset;
import org.example.pcroom.feature.user.dto.SignupRequest;
import org.example.pcroom.feature.user.dto.SignupResponse;
import org.example.pcroom.feature.user.entity.User;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setCreateDate(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return new SignupResponse(user.getEmail(), "회원가입 성공");
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(Role.USER)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        userRepository.save(user);
    }

}