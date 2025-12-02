package org.example.pcroom.global.config.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pcroom.feature.pcroom.dto.IpResultDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PcroomSeatStatusCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper; // RedisConfig의 redisObjectMapper 주입
    private static final String KEY_PREFIX = "pcroom:seatStatus:";

    /**
     * PC방 단위 DTO 전체 저장
     */
    public void savePcroomSeatStatus(Long pcroomId, List<IpResultDto.SeatStatusDto> dto) {
        String key = KEY_PREFIX + pcroomId;
        // 저장 (TTL 2분)
        redisTemplate.opsForValue().set(key, dto, Duration.ofMinutes(2));
        // 저장 직후에는 디버그 용으로 key 존재 확인만 한다 (값 형식은 Map 일 수 있음)
        Object check = redisTemplate.opsForValue().get(key);
        log.info("[savePcroomStatus] Redis 저장 후 조회 타입: {}, valueSummary: {}", check == null ? "null" : check.getClass().getSimpleName(), check == null ? "null" : summariseForLog(check));
    }

    /**
     * PC방 단위 좌석 상태 조회
     * Redis에서 꺼낸 값(Map, List 등)을 SeatStatusDto 리스트로 변환
     */
    public List<IpResultDto.SeatStatusDto> getPcroomStatus(Long pcroomId) {
        String key = KEY_PREFIX + pcroomId;
        Object raw = redisTemplate.opsForValue().get(key);

        if (raw == null) {
            log.info("[getPcroomStatus] MISS – key={}", key);
            return null;
        }

        try {
            // Redis 반환값 → SeatStatusDto 리스트로 변환
            List<IpResultDto.SeatStatusDto> dtoList = objectMapper.convertValue(raw, new TypeReference<List<IpResultDto.SeatStatusDto>>() {
            });

            log.info("[getPcroomStatus] HIT – key={}, size={}", key, dtoList == null ? 0 : dtoList.size());

            return dtoList;

        } catch (Exception e) {
            log.warn("[getPcroomStatus] convertValue failed – key={}, storedClass={}, msg={}", key, raw.getClass().getName(), e.getMessage());
            return null;
        }
    }

    // 로그에 너무 긴 객체가 찍히는걸 방지하기 위한 간단 요약기
    private String summariseForLog(Object value) {
        try {
            if (value instanceof Map) {
                Map<?, ?> m = (Map<?, ?>) value;
                return "Map(keys=" + m.keySet().size() + ")";
            }
            return value.toString();
        } catch (Exception e) {
            return "unserializable";
        }
    }
}
