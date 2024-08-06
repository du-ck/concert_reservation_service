package com.hh.consertreservation.application.facade;

import com.hh.consertreservation.domain.user.User;
import com.hh.consertreservation.domain.user.UserService;
import com.hh.consertreservation.domain.waiting.WaitingService;
import com.hh.consertreservation.support.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenFacade {
    private final UserService userService;
    private final WaitingService waitingService;
    private final RedissonClient redissonClient;


    public String issue(long userId, long maximum_ongoing_count) throws Exception {
        Optional<User> user = userService.getUser(userId);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("없는 사용자 입니다");
        }
        RLock lock = redissonClient.getLock("tokenLock");
        lock.lock();
        try {
            String issuedToken = waitingService.issue(userId, maximum_ongoing_count);
            return issuedToken;
        } finally {
            lock.unlock();
        }
    }

    public boolean verification(String token) throws Exception {
        return waitingService.verification(token);
    }

    public Optional<Long> getWaitingPosition(String token) {
        return waitingService.getWaitingPosition(token);
    }
}
