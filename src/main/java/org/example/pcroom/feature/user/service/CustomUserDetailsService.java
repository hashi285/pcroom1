package org.example.pcroom.feature.user.service;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.user.entity.User;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.example.pcroom.global.config.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // CustomUserDetails 반환
        return new CustomUserDetails(
                user.getUserId(),           // userId
                user.getEmail(),        // username
                user.getPassword(),     // password
                Collections.emptyList() // 권한 리스트 (필요시 GrantedAuthority 추가)
        );
    }
}
