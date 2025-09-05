package org.example.pcroom.feature.pcroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PcroomLock {

    private static final long COOLDOWN = 60_000; // 1분 (밀리초 단위)

    private final StringRedisTemplate redisTemplate;
    /**
     * 특정 PC방에 핑을 보낼 수 있는지 확인 후, 가능하면 핑 실행
     */
    public void pingPcRoom(Long pcRoomId) {
        String key = "ping_lock:" + pcRoomId;
        String lastPingTime = redisTemplate.opsForValue().get(key);

        long now = System.currentTimeMillis();

        if (lastPingTime != null
                && (now - Long.parseLong(lastPingTime)) < COOLDOWN) {
            throw new TooManyRequestsException("잠시 후 다시 시도해주세요.");
        }

        // 여기서 실제 핑 로직 실행
        sendPing(pcRoomId);

        // 마지막 핑 시간 기록 (TTL = 쿨타임)
        redisTemplate.opsForValue()
                .set(key, String.valueOf(now), COOLDOWN, TimeUnit.MILLISECONDS);
    }

    private void sendPing(Long pcRoomId) {
        // 실제 핑 로직 작성
        System.out.println("핑 전송 완료: PC방 ID = " + pcRoomId);
    }

    // 커스텀 예외
    public static class TooManyRequestsException extends RuntimeException {
        public TooManyRequestsException(String message) {
            super(message);
        }
    }
}