package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.UserBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBalanceJpaRepository extends JpaRepository<UserBalanceEntity, Long> {
}
