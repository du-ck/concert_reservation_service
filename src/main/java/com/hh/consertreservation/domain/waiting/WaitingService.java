package com.hh.consertreservation.domain.waiting;

import com.hh.consertreservation.domain.common.RedisRepository;
import com.hh.consertreservation.support.exception.TokenIssuedException;
import com.hh.consertreservation.support.exception.TokenVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final RedisRepository redisRepository;


    @Transactional
    public String issued(long userId, long maximum_ongoing_count) throws Exception {
        boolean result;
        String token;
        //현재 ongoing 인원수가 maximum_ongoing_count 보다 적을 경우 바로 ongoing 처리
        int activeCount = redisRepository.getActiveTokenCount();
        if (activeCount < maximum_ongoing_count) {
            // 여기서 바로 activeToken 으로 넣기
            ActiveToken activeToken = ActiveToken.builder()
                    .userId(userId).build();
            activeToken.issued();
            result = redisRepository.addActiveToken(Collections.singletonList(activeToken));
            token = activeToken.toString();
        } else {
            // 여기서는 대기열 토큰으로 넣기
            WaitingToken waitingToken = WaitingToken.builder()
                    .userId(userId).build();
            waitingToken.issued();
            result = redisRepository.addWaitingToken(waitingToken);
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
        Set<Object> waitingTokens = redisRepository.getWaitingTokensToActive(limit);
        if (!CollectionUtils.isEmpty(waitingTokens)) {
            activeTokens = waitingTokens.stream().map(m -> {
                String[] parts = m.toString().split(":");
                ActiveToken activeToken = ActiveToken.builder().build();
                activeToken.issued(parts[0], Long.valueOf(parts[1]));
                return activeToken;
            }).filter(token -> token != null).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(activeTokens)) {
                boolean result = redisRepository.addActiveToken(activeTokens);
                if (result) {
                    redisRepository.removeWaitingTokens(waitingTokens.stream()
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                }
            }
        }
    }

    public Optional<Long> getWaitingPosition(String token) {
        Optional<Long> position = redisRepository.getWaitingPosition(token);
        if (position.isPresent()) {
            return position;
        }
        throw new IllegalArgumentException("순번 조회 에러");
    }

    /**
     * 사용 X
     */
    @Transactional
    public Long expire(Long maximumOngoingCount) {
        int expireCount = waitingRepository.expire();

        //처음은 무조건 가용인원+1 이 첫 후보순위 token Id (짐작)
        Long nextWaitingId = maximumOngoingCount;

        if (expireCount > 0) {
            // 만료하고 만료시킨 만큼 순서대로 ongoing 해준다.
            //ongoing 할때 created_at 과 expires_at 과 status 수정 필요
            List<Token> waitingList = waitingRepository.getNextWaiting(expireCount);
            if (!CollectionUtils.isEmpty(waitingList)) {
                //waitingList : 다음 ongoing 예정 토큰들
                //waitingList 여기 있는 리스트를 ongoing 처리 해준다.

                //createdAt은 갱신하지 않는다. (대기시작시간을 위해...)
                //expiresAt 만 현재시간 + 5분으로 갱신해준다.
                waitingList = waitingList.stream().map(m -> m.toBuilder()
                        .status(WaitingType.ONGOING)
                        .expiresAt(LocalDateTime.now().plusMinutes(5))      //토큰만료 5분
                        .build()).toList();
                //ONGOING 처리
                int onGoingCount = waitingRepository.updateTokenOngoing(waitingList);

                //방금 ongoing 처리한 리스트중 마지막 토큰의 id를 가져온다.
                nextWaitingId = waitingList.get(waitingList.size() - 1).getId();
                nextWaitingId++;    //+1 하면 waiting상태의 토큰 중 가장 첫번째 토큰의 Id를 짐작 가능.
            }
        }
        return nextWaitingId;
    }

    public boolean verification(String token) throws Exception {
        boolean result = redisRepository.existActiveToken(token);
        if (!result) {
            throw new TokenVerificationException("토큰인증 실패");
        }
        return true;
    }

    @Transactional
    public Long expireAfterPayment(String token) throws Exception {
        boolean isExist = redisRepository.existActiveToken(token);
        if (isExist) {
            Optional<Long> expireTokenCount = redisRepository.removeExpiredTokens(Collections.singletonList(token));
            if (expireTokenCount.get() == 0) {
                throw new TokenVerificationException("토큰만료 실패");
            }
            return expireTokenCount.get();
        } else {
            throw new TokenVerificationException("토큰인증 실패");
        }
    }
}
