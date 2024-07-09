package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {
}
