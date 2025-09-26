package org.example.pcroom.feature.manager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.manager.dto.ManagerPcroomDto;
import org.example.pcroom.feature.manager.entity.PcroomManager;
import org.example.pcroom.feature.manager.repository.PcroomManagerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final PcroomManagerRepository pcroomManagerRepository;


    // 저장
    @Transactional
    public ManagerPcroomDto.addPcroomResponse assignManagerToPcroom(Long userId, Long pcroomId){
        PcroomManager pcroomManager = new PcroomManager();
        pcroomManager.setUserId(userId);
        pcroomManager.setPcroomId(pcroomId);
        pcroomManagerRepository.save(pcroomManager);

        return new ManagerPcroomDto.addPcroomResponse(
                pcroomManager.getUserId(),
                pcroomManager.getPcroomId()
        );
    }

    //삭제
    @Transactional
    public void removeManagerFromPcroom(Long userId, Long pcroomId){
        pcroomManagerRepository.deletePcroomManagerByUserIdAndPcroomId(userId, pcroomId);
    }

    // 피시방의 가동률을 리스트로 반환
    @Transactional
    public ManagerPcroomDto.findByManagerId  findByManagerId(Long userId){
        return null;
    }
}
