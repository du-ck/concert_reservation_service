package com.hh.consertreservation.infra.concert;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleJpaRepository extends JpaRepository<ScheduleEntity, Long> {

    @Query("select s from ScheduleEntity s join fetch s.concert " +
            "where s.concert.id = :concertId")
    List<ScheduleEntity> findAllByConcertId(@Param("concertId") long concertId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ScheduleEntity s join fetch s.concert c " +
            "where c.id = :concertId " +
            "and s.concertDate = :concertDateTime")
    Optional<ScheduleEntity> findScheduleIdWithLock(@Param("concertId") long concertId, @Param("concertDateTime") String concertDateTime);
}
