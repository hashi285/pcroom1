package org.example.pcroom.feature.pcroom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.SeatsDto;
import org.example.pcroom.feature.pcroom.repository.IpResultRepository;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.repository.SeatRepository;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PcRoomService {
    private final UserRepository userRepository;
    private final PingService pingService;
    private final PcroomRepository pcroomRepository;
    private final IpResultRepository ipResultRepository;
    private final SeatRepository seatRepository;


    /**
     *
     * @param pcRoomId 조회할 피시방 ID
     * @return String 형식으로 피시방 가동률  반환
     * @throws Exception
     */
    @Transactional
    public String canUseSeat(Long pcRoomId) throws Exception {
        List<String> list = pingService.update(pcRoomId);
        float alive = 0;
        for (String ip : list) {
            if(ip.equals("alive")) alive++;
        }
        float size = list.size();
        float percentage = (alive / size) * 100;

        return String.format("%.1f", percentage);
    }

//
//    /**
//     *
//     * @param pcRoomId 조회할 피시방 ID
//     * @param is_available 사용자가 사용할 자리
//     * @return 사용자가 사용 가능한 자리 Map 형식으로 반환
//     * @throws Exception
//     */
//    @Transactional
//    public Map<Integer, List<Integer>> getIpResult(Long pcRoomId, Long is_available) throws Exception {
//        List<String> pingResults = pingService.update(pcRoomId); // alive/dead
//        List<SeatsDto> seats = seatRepository.findSeatsByPcroomId(pcRoomId); // zoneNumber 정보
//
//        // zone 별 인덱스 리스트 구성
//        Map<Integer, List<Integer>> zoneToIndexes = new HashMap<>();
//        for (int i = 0; i < pingResults.size(); i++) {
//            int zone = seats.get(i).getZoneNumber();
//            zoneToIndexes.computeIfAbsent(zone, k -> new ArrayList<>()).add(i);
//        }
//
//        Map<Integer, List<Integer>> result = new HashMap<>();
//
//        for (Map.Entry<Integer, List<Integer>> entry : zoneToIndexes.entrySet()) {
//            int zone = entry.getKey();
//            List<Integer> indexes = entry.getValue();
//
//            int count = 0;
//            for (int i = 0; i < indexes.size(); i++) {
//                int idx = indexes.get(i);
//                if ("dead".equals(pingResults.get(idx))) {
//                    count++;
//                    if (count == is_available) {
//                        // 조건 만족: dead인 인덱스를 전부 모음
//                        List<Integer> deadIndexes = indexes.stream()
//                                .filter(j -> "dead".equals(pingResults.get(j)))
//                                .toList();
//                        result.put(zone, deadIndexes);
//                        break;
//                    }
//                } else {
//                    count = 0;
//                }
//            }
//        }
//        return result;
//    }


}