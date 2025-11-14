package org.example.pcroom.feature.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.entity.Pcroom;
import org.example.pcroom.feature.pcroom.entity.Seat;
import org.example.pcroom.feature.pcroom.repository.PcroomRepository;
import org.example.pcroom.feature.pcroom.repository.SeatRepository;
import org.example.pcroom.feature.pcroom.service.PingService;
import org.example.pcroom.feature.user.enums.UserRole;
import org.example.pcroom.feature.user.dto.FavoriteDto;
import org.example.pcroom.feature.user.dto.SignupRequest;
import org.example.pcroom.feature.user.dto.SignupResponse;
import org.example.pcroom.feature.user.entity.Favorite;
import org.example.pcroom.feature.user.entity.User;
import org.example.pcroom.feature.user.repository.FavoriteRepository;
import org.example.pcroom.feature.user.repository.UserRepository;
import org.example.pcroom.global.config.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FavoriteRepository favoriteRepository;
    private final PcroomRepository pcroomRepository;
    private final PingService pingService;
    private final SeatRepository seatRepository;


    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateDate(LocalDateTime.now());
        user.setNickname(request.getNickname());
        user.setRole(UserRole.USER);
        userRepository.save(user);

        return new SignupResponse(user.getEmail(), "회원가입 성공");
    }

    public UserRole userRole(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        return user.getRole();
    }

    /**
     * 즐겨찾기 추가
     * @param userId
     * @param pcroomId
     */
    @Transactional
    public void addFavorite(Long userId, Long pcroomId) {
        if (!favoriteRepository.existsByUserIdAndPcroomId(userId, pcroomId)) {
            favoriteRepository.save(new Favorite(userId, pcroomId));
        }
    }

    /**
     * 즐겨찾기 삭제
     * @param userId
     * @param itemId
     */
    @Transactional
    public void removeFavorite(Long userId, Long itemId) {
        favoriteRepository.deleteByUserIdAndPcroomId(userId, itemId);
    }

    /**
     * 즐겨찾기한 피시방 조회
     * @param userId
     * @return
     */
    @Transactional
    public List<FavoriteDto> isFavorite(Long userId) {
        return favoriteRepository.findFavoritePcroomsByUserId(userId);
    }


    private boolean hasContinuousSeats(Long pcroomId, int partySize) {
        if (pcroomId == null) return false;

        // 좌석 조회
        List<Seat> seats = seatRepository.findAvailableSeatsByPcroomId(pcroomId);
        if (seats == null || seats.isEmpty()) return false;

        // BFS 기반 인접 그룹 탐색
        List<List<Seat>> groups = findAdjacentGroups(seats);

        // 그룹 크기 체크
        return groups.stream().anyMatch(group -> group.size() >= partySize);
    }

    private List<List<Seat>> findAdjacentGroups(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) return Collections.emptyList();

        // 좌표를 key로 매핑
        Map<String, Seat> seatMap = seats.stream()
                .collect(Collectors.toMap(
                        s -> s.getX() + "," + s.getY(),
                        Function.identity(),
                        (a, b) -> a // 중복 좌표 시 첫 번째만 사용
                ));

        Set<String> visited = new HashSet<>();
        List<List<Seat>> groups = new ArrayList<>();
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}}; // 상하좌우

        for (Seat seat : seats) {
            if (seat == null) continue;

            String key = seat.getX() + "," + seat.getY();
            if (visited.contains(key)) continue;

            List<Seat> group = new ArrayList<>();
            Queue<Seat> queue = new LinkedList<>();
            queue.add(seat);

            while (!queue.isEmpty()) {
                Seat cur = queue.poll();
                if (cur == null) continue;

                int x = cur.getX();
                int y = cur.getY();
                String curKey = x + "," + y;

                if (visited.contains(curKey)) continue;
                visited.add(curKey);
                group.add(cur);

                // 상하좌우 인접 좌석 탐색
                for (int[] dir : directions) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];
                    String neighborKey = nx + "," + ny;

                    if (seatMap.containsKey(neighborKey) && !visited.contains(neighborKey)) {
                        queue.add(seatMap.get(neighborKey));
                    }
                }
            }

            if (!group.isEmpty()) groups.add(group);
        }

        return groups;
    }




    @Transactional
    public List<FavoriteDto> getFavoritesWithSeatCondition(Long userId, Integer partySize) {

        // 기본 즐겨찾기 목록 조회
        List<FavoriteDto> favorites = isFavorite(userId);

        System.out.println("userId: " + userId);
        System.out.println("partySize: " + partySize);
        System.out.println("favorites: " + favorites);


        // partySize == 1 이면 전체 반환
        if (partySize == 1) {
            System.out.println(favorites);
            return favorites;
        }

        // 2인 이상 → 연속 좌석 조건 필터링
        List<Long> pcroomIds = favorites.stream()
                .map(FavoriteDto::getPcroomId)
                .toList();

        List<Pcroom> pcrooms = pcroomRepository.findAllByPcroomIdIn(pcroomIds);

        // 조건 만족하는 피시방만 필터링
        List<Long> filteredIds = pcrooms.stream()
                .filter(pcroom -> hasContinuousSeats(pcroom.getPcroomId(), partySize))
                .map(Pcroom::getPcroomId)
                .toList();

        // FavoriteDto 목록에서 필터링된 id만 남기기
        return favorites.stream()
                .filter(dto -> filteredIds.contains(dto.getPcroomId()))
                .toList();
    }


}
