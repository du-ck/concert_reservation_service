package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleJpaRepository extends JpaRepository<ScheduleEntity, Long> {
}
