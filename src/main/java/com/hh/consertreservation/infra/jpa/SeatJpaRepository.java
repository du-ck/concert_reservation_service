package com.hh.consertreservation.infra.jpa;

import com.hh.consertreservation.infra.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {
    @Query("select s from SeatEntity s join fetch s.scheduleEntity sch " +
            "where sch.id = :scheduleId " +
            "and s.status = 'EMPTY' ")
    List<SeatEntity> findEmptySeat(@Param("scheduleId") long scheduleId);
}
