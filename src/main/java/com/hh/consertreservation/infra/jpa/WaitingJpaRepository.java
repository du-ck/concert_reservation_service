package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.WaitingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingJpaRepository extends JpaRepository<WaitingEntity, Long> {
}
