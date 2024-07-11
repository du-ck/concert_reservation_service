package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleJpaRepository extends JpaRepository<ScheduleEntity, Long> {

    @Query("select s from ScheduleEntity s join fetch s.concert " +
            "where s.concert.id = :concertId")
    List<ScheduleEntity> findAllByConcertId(@Param("concertId") long concertId);
}
