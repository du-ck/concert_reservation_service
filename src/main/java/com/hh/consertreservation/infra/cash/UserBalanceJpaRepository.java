package com.hh.consertreservation.infra.cash;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBalanceJpaRepository extends JpaRepository<UserBalanceEntity, Long> {
    Optional<UserBalanceEntity> findByUserId(long userId);
}
