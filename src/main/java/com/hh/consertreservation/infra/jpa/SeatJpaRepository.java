package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {
}
