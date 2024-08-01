package com.hh.consertreservation.application.facade;

import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.ConcertTitle;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.concert.ConcertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class  ConcertFacade {
    private final ConcertService concertService;

    public List<ConcertTitle> getConcerts() {
        return concertService.getConcerts();
    }

    public List<ConcertSchedule> getDates(long concertId) {
        return concertService.getDates(concertId);
    }

    @Transactional
    public List<Seat> getSeats(long scheduleId, String concertDateTime) {
        return concertService.getSeats(scheduleId, concertDateTime);
    }


    public Optional<Seat> reserve(long scheduleId, long seatNumber) throws Exception {
        Optional<Seat> seat = concertService.reserve(scheduleId, seatNumber);
        return seat;
    }
}
