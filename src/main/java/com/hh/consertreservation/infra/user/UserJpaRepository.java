package com.hh.consertreservation.infra.user;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity u join fetch u.userBalanceEntity " +
            "where u.id = :userId")
    Optional<UserEntity> findById(@Param("userId") long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserEntity u join fetch u.userBalanceEntity " +
            "where u.id = :userId")
    Optional<UserEntity> findByIdWithLock(@Param("userId") long userId);
}
