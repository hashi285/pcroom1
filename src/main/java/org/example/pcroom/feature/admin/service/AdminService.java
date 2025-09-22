package org.example.pcroom.feature.admin.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.pcroom.feature.admin.dto.UserListDto;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    @Transactional
    public List<UserListDto> getAllUsers() {

        return userRepository.findAll().stream()
                .map(user -> new UserListDto(
                        user.getUserId(),
                        user.getNickname(),
                        user.getEmail(),
                        user.getRole(),
                        user.getCreateDate()

                ))
                .toList();
    }
}
