package com.hh.consertreservation.infra.cash;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserBalanceJpaRepository extends JpaRepository<UserBalanceEntity, Long> {

    Optional<UserBalanceEntity> findByUserId(long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select b from UserBalanceEntity b " +
            "where b.userId = :userId")
    Optional<UserBalanceEntity> findByUserIdWithLock(@Param("userId") long userId);
}
