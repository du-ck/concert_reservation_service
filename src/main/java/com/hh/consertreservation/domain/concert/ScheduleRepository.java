package com.hh.consertreservation.domain.concert;


import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    List<ConcertSchedule> findAllDates(long concertId);

    Optional<ConcertSchedule> getScheduleId(long concertId, String concertDateTime);

    Optional<ConcertSchedule> findById(long scheduleId);
}
