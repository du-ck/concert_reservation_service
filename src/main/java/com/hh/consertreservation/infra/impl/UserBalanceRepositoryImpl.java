package com.hh.consertreservation.infra.impl;

import com.hh.consertreservation.domain.dto.UserBalance;
import com.hh.consertreservation.domain.repository.UserBalanceRepository;
import com.hh.consertreservation.infra.entity.UserBalanceEntity;
import com.hh.consertreservation.infra.jpa.UserBalanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserBalanceRepositoryImpl implements UserBalanceRepository {

    private final UserBalanceJpaRepository jpaRepository;
}
