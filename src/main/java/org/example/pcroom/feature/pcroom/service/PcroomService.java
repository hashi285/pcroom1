package org.example.pcroom.feature.pcroom.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.pcroom.feature.pcroom.dto.PcroomDto;
import org.example.pcroom.feature.pcroom.dto.PingUtilizationDto;
import org.example.pcroom.feature.pcroom.dto.SeatsDto;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.repository.SeatRepository;
import org.example.pcroom.feature.user.service.UserService;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PcroomService {
    private final PingService pingService;
    private final PcroomRepository pcroomRepository;
    private final SeatRepository seatRepository;
    private final UserService userService;


    /**
     * @param pcRoomId 조회할 피시방 ID
     * @return String 형식으로 피시방 가동률  반환
     * @throws Exception
     */
    @Transactional
    public PingUtilizationDto canUseSeat(Long pcRoomId) throws Exception {
        double utilization = pingService.ping(pcRoomId);
        Optional<Pcroom> pcroom = pcroomRepository.findByPcroomId(pcRoomId);
        String name = pcroom.get().getNameOfPcroom();
        Integer seatCount = pcroomRepository.findByPcroomId(pcRoomId)
                .map(Pcroom::getSeatCount)
                .orElse(0);

        Integer usedSeatCount = (int) (seatCount * utilization/100);



        return new PingUtilizationDto(
                pcRoomId,
                name,
                utilization,
                seatCount,
                usedSeatCount
        );
    }

    /**
     * 피시방 저장
     *
     * @param request
     * @return
     */
    @Transactional
    public PcroomDto.ReadPcRoomResponse registerNewPcroom(PcroomDto.CreatePcRoomRequest request) {

        var pcroom = Pcroom.register(
                request.getNameOfPcroom(),
                request.getSeatCount(),
                request.getPort(),
                request.getWidth(),
                request.getHeight()
        );
        pcroomRepository.save(pcroom);

        return new PcroomDto.ReadPcRoomResponse(
                pcroom.getPcroomId(),
                pcroom.getNameOfPcroom(),
                pcroom.getSeatCount(),
                pcroom.getPort(),
                pcroom.getWidth(),
                pcroom.getHeight()
        );
    }

    /**
     * 피시방 자리 저장
     * @param seatsDtos
     * @return
     */
    @Transactional
    public List<SeatsDto> registerNewSeat(List<SeatsDto> seatsDtos) {
        String nameOfPcroom = seatsDtos.getFirst().getNameOfPcroom();
        Pcroom pcroom = pcroomRepository.findByNameOfPcroom(nameOfPcroom)
                .orElseThrow(() -> new IllegalArgumentException("해당 피시방 없음: " + nameOfPcroom));
        int seatNum = pcroom.getSeatCount();
        if(seatsDtos.size() != seatNum) {
            throw new IllegalArgumentException(nameOfPcroom + "피시방은 좌석을 " + seatNum + "개만 입력이 가능합니다.");
        }

        List<Seat> seats = seatsDtos.stream()
                .map(dto -> dto.toEntity(pcroom))
                .toList();

        seatRepository.saveAll(seats);
        return null;
    }

    /**
     * 피시방 검색
     * @param name
     * @return
     */
    @Transactional
    public List<PcroomDto> searchPcrooms(String name) {
        return pcroomRepository.findByNameOfPcroomContaining(name).stream()
                .map(PcroomDto::fromEntity)
                .toList();
    }

    /**
     * 피시방 자리추천 알고리즘
     * @param partySize
     * @return
     */
//    @SneakyThrows
//    @Transactional
//    public List<Pcroom> recommendation(Integer partySize, Long userId) throws ExecutionException {
//        List<String> favorite = userService.isFavorite(userId);
//        List<Pcroom> pcroom1 = pcroomRepository.findByNameOfPcroomIn(favorite);
//        List<Long> id = pcroom1.stream()
//                        .map(Pcroom::getPcroomId)
//                                .toList();
//
//        for (int i = 0; i < id.size(); i++){
//
//            Long ia = id.get(i);
//            System.out.println(ia);
//            pingService.ping(ia);
//        }
//
//        List<Pcroom> result = pcroom1.stream()
//                .filter(pcroom -> hasContinuousSeats(pcroom.getPcroomId(), partySize))
//                .toList();
//
//        return result;
//    }

    // 특정 피시방에 partySize 만큼 연속된 자리 있는지 체크
    private boolean hasContinuousSeats(Long pcroomId, int partySize) {
        // 사용 가능한 좌석 조회
        List<Seat> availableSeats = seatRepository.findAvailableSeatsByPcroomId(pcroomId);

        // 좌표 기반 BFS/DFS 탐색으로 그룹화
        List<List<Seat>> groups = findAdjacentGroups(availableSeats);

        // 그룹 중 partySize 이상인 그룹이 있으면 true
        return groups.stream().anyMatch(group -> group.size() >= partySize);
    }

    // BFS/DFS 로 붙어있는 자리 그룹 찾기
    private List<List<Seat>> findAdjacentGroups(List<Seat> seats) {
        Map<String, Seat> seatMap = seats.stream()
                .collect(Collectors.toMap(s -> s.getX() + "," + s.getY(), Function.identity()));

        Set<String> visited = new HashSet<>();
        List<List<Seat>> groups = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}}; // 상하좌우

        for (Seat seat : seats) {
            String key = seat.getX() + "," + seat.getY();
            if (visited.contains(key)) continue;

            List<Seat> group = new ArrayList<>();
            Queue<Seat> queue = new LinkedList<>();
            queue.add(seat);

            while (!queue.isEmpty()) {
                Seat cur = queue.poll();
                String curKey = cur.getX() + "," + cur.getY();
                if (visited.contains(curKey)) continue;
                visited.add(curKey);
                group.add(cur);

                for (int[] dir : directions) {
                    String neighborKey = (cur.getX() + dir[0]) + "," + (cur.getY() + dir[1]);
                    if (seatMap.containsKey(neighborKey) && !visited.contains(neighborKey)) {
                        queue.add(seatMap.get(neighborKey));
                    }
                }
            }

            groups.add(group);
        }

        return groups;
    }


    @Transactional
    public PcroomDto.PcroomInfo getPcroomInfo(Long pcroomId) {
        Pcroom pcroom = pcroomRepository.findById(pcroomId)
                .orElseThrow(() -> new EntityNotFoundException("해당 피시방을 찾을 수 없습니다. ID=" + pcroomId));

        return new PcroomDto.PcroomInfo(
                pcroom.getNameOfPcroom(),
                pcroom.getSeatCount()
        );
    }
}