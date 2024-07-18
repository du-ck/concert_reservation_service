package com.hh.consertreservation.infra.cash;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {
    Optional<ReservationEntity> findByUserIdAndScheduleId(long userId, long scheduleId);

}
