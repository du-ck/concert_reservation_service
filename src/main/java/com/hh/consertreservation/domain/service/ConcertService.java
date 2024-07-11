package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.dto.servicerequest.ReservationServiceRequestDto;
import com.hh.consertreservation.domain.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ScheduleRepository scheduleRepository;

    public List<ConcertSchedule> getDates(long concertId) {
        return scheduleRepository.findAllDates(concertId);
    }

    public List<Seat> getSeats(long concertId, String concertDateTime) {
        return new ArrayList<>();
    }

    public Optional<Seat> reservation(ReservationServiceRequestDto req) {
        return Optional.empty();
    }
}
