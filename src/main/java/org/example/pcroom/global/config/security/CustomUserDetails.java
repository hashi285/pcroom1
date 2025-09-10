package org.example.pcroom.global.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // 생성자
    public CustomUserDetails(Long userId, String username, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    // Custom 필드 getter
    public Long getUserId() {
        return userId;
    }

    // UserDetails 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }
}

