package com.hh.consertreservation.domain.concert;


import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    List<ConcertSchedule> findAllDates(long concertId);

    Optional<ConcertSchedule> getScheduleIdWithLock(long concertId, String concertDateTime);

    Optional<ConcertSchedule> findById(long scheduleId);
}
