package com.hh.consertreservation.application.facade;

import com.hh.consertreservation.domain.dto.ConcertSchedule;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private final ConcertService concertService;

    public List<ConcertSchedule> getDates(long concertId) {
        return concertService.getDates(concertId);
    }

    public List<Seat> getSeats(long scheduleId, String concertDateTime) {
        return concertService.getSeats(scheduleId, concertDateTime);
    }

    public Optional<Seat> reservation(long scheduleId, long seatNumber) throws Exception {
        return concertService.reservation(scheduleId, seatNumber);
    }
}
