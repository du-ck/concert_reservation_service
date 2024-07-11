package com.hh.consertreservation.domain.repository;


import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;

import java.util.List;

public interface ScheduleRepository {
    List<ConcertSchedule> findAllDates(long concertId);
}
