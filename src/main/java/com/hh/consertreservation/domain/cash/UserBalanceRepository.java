package com.hh.consertreservation.domain.cash;


import java.util.Optional;

public interface UserBalanceRepository {
    Optional<UserBalance> findByUserId(long userId);
    Optional<UserBalance> findByUserIdWithLock(long userId);
    Optional<UserBalance> save(UserBalance balance);
}
