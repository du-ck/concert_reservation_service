package com.hh.consertreservation.infra.concert;

import com.hh.consertreservation.domain.concert.SeatType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {
    @Query("select s from SeatEntity s join fetch s.scheduleEntity sch " +
            "where sch.id = :scheduleId " +
            "and s.status = 'EMPTY' ")
    List<SeatEntity> findEmptySeat(@Param("scheduleId") long scheduleId);


    @Lock(LockModeType.OPTIMISTIC)
    @Query("select s from SeatEntity s " +
            "where s.scheduleId = :scheduleId " +
            "and s.seatNumber = :seatNumber " +
            "and s.status = 'EMPTY'")
    Optional<SeatEntity> findSeatForReserveWithLock(@Param("scheduleId") long scheduleId, @Param("seatNumber") long seatNumber);

    @Modifying(clearAutomatically = true)
    @Query(value = "update SeatEntity s set s.status = 'EMPTY' " +
            "where s.updatedAt < :expireDateTime " +
            "and s.status = 'TEMPORARILY' ")
    int setEmptyForTempSeat(@Param("expireDateTime")LocalDateTime expireDateTime);

    Optional<SeatEntity> findByIdAndStatus(long id, SeatType status);

    List<SeatEntity> findByScheduleId(long scheduleId);
}
