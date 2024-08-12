package com.hh.consertreservation.infra.waiting;

import com.hh.consertreservation.domain.waiting.WaitingRepository;
import com.hh.consertreservation.domain.waiting.ActiveToken;
import com.hh.consertreservation.domain.waiting.WaitingToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class WaitingRedisRepository implements WaitingRepository {

    private final static String WAITING_TOKEN_KEY = "WAIT";
    private final static String ACTIVE_TOKEN_KEY = "ACTIVE";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean addWaitingToken(WaitingToken waitingToken) {
        return redisTemplate.opsForZSet().add(WAITING_TOKEN_KEY, waitingToken.getToken(), waitingToken.getRequestTime());
    }

    @Override
    public Optional<Long> getWaitingPosition(String tokenWithUserid) {
        return Optional.of(redisTemplate.opsForZSet().rank(WAITING_TOKEN_KEY, tokenWithUserid));
    }

    @Override
    public Optional<Long> removeWaitingTokens(List<String> waitingTokens) {
        return Optional.of(redisTemplate.opsForZSet().remove(WAITING_TOKEN_KEY, waitingTokens.toArray()));
    }

    /**
     * 주어진 제한 수(limit)만큼의 활성화할 토큰 목록을 가져온다.
     */
    @Override
    public Set<Object> getWaitingTokensToActive(int limit) {
        return redisTemplate.opsForZSet().range(WAITING_TOKEN_KEY, 0, limit - 1);
    }

    @Override
    public boolean addActiveToken(List<ActiveToken> tokens) {
        long addCount = 0L;
        for (ActiveToken token : tokens) {
            String key = String.format("%s:%s", ACTIVE_TOKEN_KEY, token.toString());
            addCount += redisTemplate.opsForSet().add(key, String.valueOf(token.getUserId()));
            if (addCount > 0) {
                return redisTemplate.expire(key, 300, TimeUnit.SECONDS);   //5분 TTL
            }
        }
        return false;
    }

    @Override
    public int getActiveTokenCount() {
        ScanOptions options = ScanOptions.scanOptions().match(ACTIVE_TOKEN_KEY + "*").build();
        Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(redisConnection -> redisConnection.scan(options));

        int count = 0;
        while (cursor.hasNext()) {
            cursor.next();
            count++;
        }
        return count;
    }

    @Override
    public boolean existActiveToken(String token) {
        String key = String.format("%s:%s", ACTIVE_TOKEN_KEY, token);
        return redisTemplate.hasKey(key);
    }

    @Override
    public Optional<Long> removeActiveToken(String activeTokens) {
        String key = String.format("%s:%s", ACTIVE_TOKEN_KEY, activeTokens);
        String value = key.substring(key.lastIndexOf(":") + 1);

        return Optional.of(redisTemplate.opsForSet().remove(key, value));
    }
}
