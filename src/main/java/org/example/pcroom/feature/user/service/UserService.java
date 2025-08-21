package org.example.pcroom.feature.user.service;

import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;



}
