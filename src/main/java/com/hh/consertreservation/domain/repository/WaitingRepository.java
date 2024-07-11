package com.hh.consertreservation.domain.repository;


import com.hh.consertreservation.domain.dto.Token;
import com.hh.consertreservation.domain.dto.types.WaitingType;

import java.util.List;
import java.util.Optional;

public interface WaitingRepository {
    int expire();
    Optional<Token> addToken(long userId, String uuid, WaitingType status);
    int getOnGoingCount();
    List<Token> getNextWaiting(int limit);
    int updateTokenOngoing(List<Token> waitingTokens);

    Optional<Token> getOngoingToken(long userId, String token);
}
