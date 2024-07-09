package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<ConcertEntity, Long> {
}
