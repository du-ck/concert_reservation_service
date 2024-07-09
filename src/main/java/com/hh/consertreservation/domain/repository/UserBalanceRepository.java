package com.hh.consertreservation.domain.repository;


import com.hh.consertreservation.domain.dto.UserBalance;

import java.util.Optional;

public interface UserBalanceRepository {
    Optional<UserBalance> findByUserId(long userId);
    Optional<UserBalance> save(UserBalance balance);
}
