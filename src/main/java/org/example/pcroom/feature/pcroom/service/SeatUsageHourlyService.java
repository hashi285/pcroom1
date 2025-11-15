package org.example.pcroom.feature.pcroom.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pcroom.feature.pcroom.dto.IpResultDto;
import org.example.pcroom.feature.pcroom.entity.SeatUsageHourly;
import org.example.pcroom.feature.pcroom.repository.SeatUsageHourlyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatUsageHourlyService {

    private final PingService pingService;
    private final SeatUsageHourlyRepository seatUsageHourlyRepository;

    /**
     * 특정 피시방 30분 단위 사용량 기록
     * 동일 시간 중복 기록 방지
     */
    @Transactional
    public void recordHourlyUsage(Long pcroomId) throws ExecutionException, InterruptedException {
        // 1. Ping 수행 → IpResult DB 저장
        pingService.ping(pcroomId);

        // 2. 최신 좌석별 상태 조회
        List<IpResultDto.SeatStatusDto> latestResults = pingService.getLatestSeatResults(pcroomId);

        // Map으로 변환: seatNum -> alive 여부
        Map<Integer, Boolean> seatAliveMap = latestResults.stream()
                .collect(Collectors.toMap(
                        IpResultDto.SeatStatusDto::getSeatsNum,
                        IpResultDto.SeatStatusDto::getResult
                ));

        // 3. 현재 시간 30분 단위로 truncate
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        // 4. DB에서 동일 시간/PC방 기록 조회
        List<SeatUsageHourly> existingRecords =
                seatUsageHourlyRepository.findByPcroomIdAndCreatedAt(pcroomId, now);

        Set<Long> existingSeatIds = existingRecords.stream()
                .map(SeatUsageHourly::getSeatId)
                .collect(Collectors.toSet());

        // 5. 신규 기록만 생성
        List<SeatUsageHourly> hourlyList = seatAliveMap.entrySet().stream()
                .filter(e -> !existingSeatIds.contains(Long.valueOf(e.getKey())))
                .map(e -> {
                    SeatUsageHourly usage = new SeatUsageHourly();
                    usage.setPcroomId(pcroomId);
                    usage.setSeatId(Long.valueOf(e.getKey()));
                    usage.setCreatedAt(now);
                    usage.setUsedSeconds(e.getValue() ? 1800 : 0); // alive → 30분
                    return usage;
                }).toList();

        // 6. DB 저장
        if (!hourlyList.isEmpty()) {
            seatUsageHourlyRepository.saveAll(hourlyList);
        }
    }

}
