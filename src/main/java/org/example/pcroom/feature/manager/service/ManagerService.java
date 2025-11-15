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
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final CompetitorRelationRepository competitorRelationRepository;
    private final PcroomRepository pcroomRepository;
    private final PcroomHourlyUtilizationRepository pcroomHourlyUtilizationRepository;
    private final PcroomManagerRepository pcroomManagerRepository;

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



    // 경쟁 피시방 조회
    @Transactional
    public List<ManagerPcroomDto.PcroomManager> findPcroom(Long userId) {
        List<Long> pcroomIdList = competitorRelationRepository.findPcroomIdByUserId(userId);

        return pcroomIdList.stream()
                .map(pcroomId -> {
                    String name = pcroomRepository.findNameByPcroomId(pcroomId);
                    return new ManagerPcroomDto.PcroomManager(pcroomId, name);
                })
                .toList();
    }

    // 피시방 리스트 + 최근 n시간 조회
    @Transactional
    public List<ManagerPcroomDto.FindHourlyUtilization> getHourlyUtilization(List<Long> pcroomIds, int hours) {
        LocalDateTime fromTime = LocalDateTime.now().minusHours(hours);

        List<PcroomHourlyUtilization> records =
                pcroomHourlyUtilizationRepository.findByPcroomIdInAndTimeAfter(pcroomIds, fromTime);

        // 중복 제거: pcroomId + hour 단위 time
        Map<String, PcroomHourlyUtilization> uniqueMap = new HashMap<>();

        for (PcroomHourlyUtilization r : records) {
            // Hour 단위로 normalize
            LocalDateTime hourKey = r.getTime().withMinute(0).withSecond(0).withNano(0);

            String key = r.getPcroomId() + "_" + hourKey.toString();

            // 최신 데이터만 유지
            if (!uniqueMap.containsKey(key) ||
                    uniqueMap.get(key).getTime().isBefore(r.getTime())) {
                uniqueMap.put(key, r);
            }
        }

        return uniqueMap.values().stream()
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
                .toList();
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
