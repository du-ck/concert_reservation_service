package com.hh.consertreservation.domain.waiting;


import java.util.List;
import java.util.Optional;

public interface WaitingRepository {
    int expire();
    Optional<Token> addToken(long userId, String uuid, WaitingType status);
    int getOnGoingCount();
    List<Token> getNextWaiting(int limit);
    int updateTokenOngoing(List<Token> waitingTokens);
    Optional<Token> getOngoingToken(long userId, String token);
    int expireTokenById(long userId, String token);
}
