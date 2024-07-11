package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.Token;
import com.hh.consertreservation.domain.dto.types.WaitingType;
import com.hh.consertreservation.domain.repository.WaitingRepository;
import com.hh.consertreservation.exception.TokenIssuedException;
import com.hh.consertreservation.exception.TokenVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingService {

    private final WaitingRepository waitingRepository;


    public Optional<Token> issued(long userId, long maximum_ongoing_count) throws Exception {
        //waiting 상태의 토큰을 userId 기준으로 insert 한다.
        UUID uuid = UUID.randomUUID();
        WaitingType status = WaitingType.WAITING;

        //현재 ongoing 인원수가 maximum_ongoing_count 보다 적을 경우 바로 ongoing 처리
        int onGoingcount = waitingRepository.getOnGoingCount();
        if (onGoingcount < maximum_ongoing_count) {
            status = WaitingType.ONGOING;
        }
        Optional<Token> token = waitingRepository.addToken(userId, uuid.toString(), status);
        if (!token.isPresent()) {
            throw new TokenIssuedException("토큰 발급 실패");
        }
        return token;
    }

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

    public boolean verification(long userId, String token) throws Exception {
        Optional<Token> ongoingToken = waitingRepository.getOngoingToken(userId, token);
        if (!ongoingToken.isPresent()) {
            throw new TokenVerificationException("토큰인증 실패");
        }
        return true;
    }

    @Transactional
    public int expireAfterPayment(long userId, String token) throws Exception {
        int expireTokenCount = waitingRepository.expireTokenById(userId, token);
        if (expireTokenCount == 0) {
            throw new TokenVerificationException("토큰만료 실패");
        }
        return expireTokenCount;
    }
}
