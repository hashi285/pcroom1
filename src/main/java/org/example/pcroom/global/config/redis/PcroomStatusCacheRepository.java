package org.example.pcroom.global.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pcroom.feature.pcroom.dto.PingUtilizationDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PcroomStatusCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper; // RedisConfig의 redisObjectMapper 주입
    private static final String KEY_PREFIX = "pcroom:status:";

    /**
     * PC방 단위 DTO 전체 저장
     */
    public void savePcroomStatus(PingUtilizationDto.UtilizationAndResults dto) {
        String key = KEY_PREFIX + dto.getPcroomId();
        // 저장 (TTL 2분)
        redisTemplate.opsForValue().set(key, dto, Duration.ofMinutes(2));
        // 저장 직후에는 디버그 용으로 key 존재 확인만 한다 (값 형식은 Map 일 수 있음)
        Object check = redisTemplate.opsForValue().get(key);
        log.info("[savePcroomStatus] Redis 저장 후 조회 타입: {}, valueSummary: {}",
                check == null ? "null" : check.getClass().getSimpleName(),
                check == null ? "null" : summariseForLog(check));
    }

    /**
     * PC방 단위 DTO 전체 조회
     *
     * Redis에서 가져온 Object는 GenericJackson2JsonRedisSerializer 역직렬화 결과로 Map일 가능성이 크다.
     * 따라서 ObjectMapper.convertValue를 이용해 명시적으로 DTO로 변환한다.
     */
    public PingUtilizationDto.UtilizationAndResults getPcroomStatus(Long pcroomId) {
        String key = KEY_PREFIX + pcroomId;
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            log.info("[getPcroomStatus] Redis MISS - key={}", key);
            return null;
        }

        try {
            // Map 또는 이미 DTO인 경우 모두 처리
            PingUtilizationDto.UtilizationAndResults dto =
                    objectMapper.convertValue(obj, PingUtilizationDto.UtilizationAndResults.class);
            log.info("[getPcroomStatus] Redis HIT - key={}, dtoTime={}", key, dto == null ? "null" : dto.getNow());
            return dto;
        } catch (IllegalArgumentException e) {
            log.warn("[getPcroomStatus] Redis 값 변환 실패 - key={}, class={}, msg={}", key,
                    obj.getClass().getName(), e.getMessage());
            return null;
        }
    }

    /**
     * TTL 초기화 또는 삭제 가능
     */
    public void deletePcroomStatus(Long pcroomId) {
        String key = KEY_PREFIX + pcroomId;
        redisTemplate.delete(key);
    }

    // 로그에 너무 긴 객체가 찍히는걸 방지하기 위한 간단 요약기
    private String summariseForLog(Object value) {
        try {
            if (value instanceof Map) {
                Map<?,?> m = (Map<?,?>) value;
                return "Map(keys=" + m.keySet().size() + ")";
            }
            return value.toString();
        } catch (Exception e) {
            return "unserializable";
        }
    }
}
