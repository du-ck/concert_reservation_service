package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.dto.servicerequest.ReservationServiceRequestDto;
import com.hh.consertreservation.domain.repository.ScheduleRepository;
import com.hh.consertreservation.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;

    public List<ConcertSchedule> getDates(long concertId) {
        return scheduleRepository.findAllDates(concertId);
    }

    public List<Seat> getSeats(long concertId, String concertDateTime) {
        //concertId 와 concertDateTime 으로 스케쥴id를 찾는다.
        Optional<ConcertSchedule> schedule = scheduleRepository.getScheduleId(concertId, concertDateTime);
        if (schedule.isPresent()) {
            return seatRepository.getSeats(schedule.get().getScheduleId());
        }
        return new ArrayList<>();
    }

    public Optional<Seat> reservation(ReservationServiceRequestDto req) {
        return Optional.empty();
    }
}
