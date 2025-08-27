package org.example.pcroom.feature.user.service;

import org.example.pcroom.feature.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email) // 이메일로 조회
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())      // UserDetails에서는 username 필드로 이메일 넣어도 됨
                        .password(user.getPassword())  // 반드시 암호화된 비밀번호
                        .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }
}
