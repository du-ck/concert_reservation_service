package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.BalanceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceHistoryJpaRepository extends JpaRepository<BalanceHistoryEntity, Long> {
}
