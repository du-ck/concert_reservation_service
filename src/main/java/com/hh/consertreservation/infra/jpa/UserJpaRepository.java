package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u from UserEntity u join fetch u.userBalanceEntity " +
            "where u.id = :userId")
    Optional<UserEntity> findById(@Param("userId") long userId);
}
