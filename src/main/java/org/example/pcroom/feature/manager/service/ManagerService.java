package org.example.pcroom.feature.manager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.manager.dto.ManagerPcroomDto;
import org.example.pcroom.feature.manager.entity.PcroomHourlyUtilization;
import org.example.pcroom.feature.manager.entity.PcroomManager;
import org.example.pcroom.feature.manager.repository.PcroomHourlyUtilizationRepository;
import org.example.pcroom.feature.manager.repository.PcroomManagerRepository;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Utilization;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.repository.UtilizationRepository;
import org.example.pcroom.feature.pcroom.service.PingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final PcroomManagerRepository pcroomManagerRepository;
    private final PingService pingService;
    private final UtilizationRepository utilizationRepository;
    private final PcroomRepository pcroomRepository;
    private final PcroomHourlyUtilizationRepository pcroomHourlyUtilizationRepository;

    // 저장
    @Transactional
    public ManagerPcroomDto.addPcroomResponse assignManagerToPcroom(Long userId, Long pcroomId){

        List<Long> ListUser = pcroomManagerRepository.findPcroomIdByUserId(userId);
        if (ListUser.contains(pcroomId)){
            return null;
        }

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
    public List<ManagerPcroomDto.FindByManagerId> findByManagerId(Long userId) {
        List<Long> pcroomIdList = pcroomManagerRepository.findPcroomIdByUserId(userId);

        // 최신화
        for (Long pcroomId : pcroomIdList) {
            try {
                pingService.ping(pcroomId);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 가동률 조회
        List<Utilization> utilizationList = utilizationRepository.findAllByPcroomIdInOrderByTimeDesc(pcroomIdList);

        // Utilization -> DTO 변환 (pcroomRepository로 이름 조회)
        return utilizationList.stream()
                .map(util -> {
                    String pcroomName = pcroomRepository.findById(util.getPcroomId())
                            .map(Pcroom::getNameOfPcroom)
                            .orElse("Unknown");

                    return new ManagerPcroomDto.FindByManagerId(
                            util.getPcroomId(),
                            pcroomName,
                            util.getUtilization(),
                            util.getTime()

                    );
                })
                .toList();
    }

    // 피시방 리스트 + 최근 n시간 조회
    @Transactional
    public List<ManagerPcroomDto.FindHourlyUtilization> getHourlyUtilization(List<Long> pcroomIds, int hours) {
        LocalDateTime fromTime = java.time.LocalDateTime.now().minusHours(hours);

        List<PcroomHourlyUtilization> records = pcroomHourlyUtilizationRepository.findByPcroomIdInAndTimeAfter(pcroomIds, fromTime);

        return records.stream()
                .map(r -> {
                    String name = pcroomRepository.findById(r.getPcroomId())
                            .map(Pcroom::getNameOfPcroom)
                            .orElse("Unknown");
                    return new ManagerPcroomDto.FindHourlyUtilization(
                            r.getPcroomId(),
                            name,
                            r.getUtilization(),
                            r.getTime()
                    );
                })
                .collect(Collectors.toList());
    }


}
