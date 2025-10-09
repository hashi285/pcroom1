package org.example.pcroom.feature.manager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.manager.dto.ManagerPcroomDto;
import org.example.pcroom.feature.manager.entity.PcroomHourlyUtilization;
import org.example.pcroom.feature.manager.entity.CompetitorRelation;
import org.example.pcroom.feature.manager.entity.PcroomManager;
import org.example.pcroom.feature.manager.repository.PcroomHourlyUtilizationRepository;
import org.example.pcroom.feature.manager.repository.CompetitorRelationRepository;
import org.example.pcroom.feature.manager.repository.PcroomManagerRepository;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Utilization;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.repository.UtilizationRepository;
import org.example.pcroom.feature.pcroom.service.PingService;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final CompetitorRelationRepository competitorRelationRepository;
    private final PingService pingService;
    private final UtilizationRepository utilizationRepository;
    private final PcroomRepository pcroomRepository;
    private final PcroomHourlyUtilizationRepository pcroomHourlyUtilizationRepository;
    private final PcroomManagerRepository pcroomManagerRepository;
    private final UserRepository userRepository;

    // 저장
    @Transactional
    public ManagerPcroomDto.addPcroomResponse assignManagerToPcroom(Long userId, Long pcroomId){

        List<Long> ListUser = competitorRelationRepository.findPcroomIdByUserId(userId);
        if (ListUser.contains(pcroomId)){
            return null;
        }

        CompetitorRelation pcroomManager = new CompetitorRelation();
        pcroomManager.setUserId(userId);
        pcroomManager.setPcroomId(pcroomId);
        competitorRelationRepository.save(pcroomManager);

        return new ManagerPcroomDto.addPcroomResponse(
                pcroomManager.getUserId(),
                pcroomManager.getPcroomId()
        );
    }

    //삭제
    @Transactional
    public void removeManagerFromPcroom(Long userId, Long pcroomId){
        competitorRelationRepository.deletePcroomManagerByUserIdAndPcroomId(userId, pcroomId);
    }



    // 피시방의 가동률을 리스트로 반환
    @Transactional
    public List<ManagerPcroomDto.FindByManagerId> findByManagerId(Long userId) {
        List<Long> pcroomIdList = competitorRelationRepository.findPcroomIdByUserId(userId);

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

    // 사용자가 운영중인 피시방을 보여준다.
    @Transactional
    public List<ManagerPcroomDto.PcroomManager> getManagerPcroom(Long userId) {
        // 1️⃣ userId로 운영 중인 PcroomManager 리스트 조회
        List<PcroomManager> managers = pcroomManagerRepository.findAllByUserId(userId);

        // 2️⃣ pcroomId 목록 추출
        List<Long> pcroomIds = managers.stream()
                .map(PcroomManager::getPcroomId)
                .toList();

        // 3️⃣ 피시방 이름 한번에 조회 (N+1 방지)
        Map<Long, String> pcroomNames = pcroomRepository.findAllById(pcroomIds).stream()
                .collect(Collectors.toMap(Pcroom::getPcroomId, Pcroom::getNameOfPcroom));


        // 4️⃣ DTO 변환
        return managers.stream()
                .map(manager -> new ManagerPcroomDto.PcroomManager(
                        manager.getPcroomId(),
                        pcroomNames.getOrDefault(manager.getPcroomId(), "삭제된 피시방")
                ))
                .toList();
    }


    // 사용자가 운영중인 피시방을 저장한다.
    @Transactional
    public void setManagerPcroom(Long userId, Long pcroomId){
        PcroomManager pcroomManager = new PcroomManager();
        pcroomManager.setUserId(userId);
        pcroomManager.setPcroomId(pcroomId);
        pcroomManagerRepository.save(pcroomManager);
    }

    // 사용자가 운영중인 피시방을 삭제한다.
    @Transactional
    public void deleteManagerFromPcroom(Long userId, Long pcroomId){
        pcroomManagerRepository.deleteByUserIdAndPcroomId(userId, pcroomId);
    }
}
