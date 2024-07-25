package com.hh.consertreservation.infra.cash;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByUserIdAndScheduleId(long userId, long scheduleId);

}
