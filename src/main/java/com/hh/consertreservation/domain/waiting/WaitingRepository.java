package com.hh.consertreservation.domain.waiting;

import com.hh.consertreservation.domain.waiting.ActiveToken;
import com.hh.consertreservation.domain.waiting.WaitingToken;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WaitingRepository {
    //Waiting Token 관리
    /**
     * 대기열 토큰 추가
     */
    boolean addWaitingToken(WaitingToken waitingToken);

    /**
     * 특정 토큰의 대기 위치를 조회
     */
    Optional<Long> getWaitingPosition(String tokenWithUserid);

    Optional<Long> removeWaitingTokens(List<String> waitingTokens);

    /**
     * 활성화할 대기열 토큰 목록 조회
     */
    Set<Object> getWaitingTokensToActive(int limit);

    // Active Token 관리

    /**
     * 활성 토큰 추가
     */
    boolean addActiveToken(List<ActiveToken> tokens);

    /**
     * 모든 활성 토큰을 조회
     */
    int getActiveTokenCount();

    /**
     * 활성 토큰 조회
     */
    boolean existActiveToken(String token);

    /**
     * 결제 후 활성토큰 만료처리
     */
    Optional<Long> removeActiveToken(String activeTokens);
}
