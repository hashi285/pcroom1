package org.example.pcroom.feature.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.pcroom.feature.admin.dto.PcroomListDto;
import org.example.pcroom.feature.admin.dto.UserListDto;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PcroomRepository pcroomRepository;

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

    @Transactional
    public List<PcroomListDto> getAllPcroom() {
        List<Pcroom> pcroomList = pcroomRepository.findAllByOrderByNameOfPcroom();
        return pcroomList.stream()
                .map(pc -> new PcroomListDto(pc.getPcroomId(), pc.getNameOfPcroom(), pc.getSeatCount()))
                .toList();
    }

    /**
     * 피시방 삭제
     * @param pcroomId 피시방 아이디를 이용하여 삭제
     */
    @Transactional
    public void removePcroom(Long pcroomId) {
        pcroomRepository.deleteById(pcroomId);
    }
}
