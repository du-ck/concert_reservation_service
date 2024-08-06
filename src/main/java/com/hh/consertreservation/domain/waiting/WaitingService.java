package com.hh.consertreservation.domain.waiting;

import com.hh.consertreservation.support.exception.TokenIssuedException;
import com.hh.consertreservation.support.exception.TokenVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingService {
    private final WaitingRepository waitingRepository;


    @Transactional
    public String issue(long userId, long maximum_ongoing_count) throws Exception {
        boolean result;
        String token;
        //현재 ongoing 인원수가 maximum_ongoing_count 보다 적을 경우 바로 ongoing 처리
        int activeCount = waitingRepository.getActiveTokenCount();
        if (activeCount < maximum_ongoing_count) {
            // 여기서 바로 activeToken 으로 넣기
            ActiveToken activeToken = ActiveToken.builder()
                    .userId(userId).build();
            activeToken.issue();
            result = waitingRepository.addActiveToken(Collections.singletonList(activeToken));
            token = activeToken.toString();
        } else {
            // 여기서는 대기열 토큰으로 넣기
            WaitingToken waitingToken = WaitingToken.builder()
                    .userId(userId).build();
            waitingToken.issue();
            result = waitingRepository.addWaitingToken(waitingToken);
            token = waitingToken.getToken();
        }
        if (!result) {
            throw new TokenIssuedException("토큰 발급 실패");
        }
        return token;
    }

    @Transactional
    public void active(int limit) {
        List<ActiveToken> activeTokens = new ArrayList<>();
        //active 시킬 waiting token 들 조회
        Set<Object> waitingTokens = waitingRepository.getWaitingTokensToActive(limit);
        if (!CollectionUtils.isEmpty(waitingTokens)) {
            waitingTokens.stream().forEach(item -> {
                //대기열 삭제
                Optional<Long> deleteWaiting = waitingRepository.removeWaitingTokens(Collections.singletonList(item.toString()));
                if (deleteWaiting.isPresent()) {
                    //active 활성화
                    String[] parts = item.toString().split(":");
                    ActiveToken activeToken = ActiveToken.builder().build();
                    activeToken.issue(parts[0], Long.valueOf(parts[1]));
                    waitingRepository.addActiveToken(Collections.singletonList(activeToken));
                }
            });
        }
    }

    public Optional<Long> getWaitingPosition(String token) {
        Optional<Long> position = waitingRepository.getWaitingPosition(token);
        if (position.isPresent()) {
            return position;
        }
        throw new IllegalArgumentException("순번 조회 에러");
    }

    public boolean verification(String token) throws Exception {
        boolean result = waitingRepository.existActiveToken(token);
        if (!result) {
            throw new TokenVerificationException("토큰인증 실패");
        }
        return true;
    }

    @Transactional
    public Long expireAfterPayment(String token) throws Exception {
        boolean isExist = waitingRepository.existActiveToken(token);
        if (isExist) {
            Optional<Long> expireTokenCount = waitingRepository.removeExpiredTokens(Collections.singletonList(token));
            if (expireTokenCount.get() == 0) {
                throw new TokenVerificationException("토큰만료 실패");
            }
            return expireTokenCount.get();
        } else {
            throw new TokenVerificationException("토큰인증 실패");
        }
    }
}
