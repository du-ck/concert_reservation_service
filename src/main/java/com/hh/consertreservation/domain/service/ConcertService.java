package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.dto.servicerequest.ReservationServiceRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConcertService {

    public Optional<List<Concert>> getDates(long concertId) {
        return Optional.empty();
    }

    public Optional<List<Seat>> getSeats(long concertId, String concertDateTime) {
        return Optional.empty();
    }

    public Optional<Seat> reservation(ReservationServiceRequestDto req) {
        return Optional.empty();
    }
}
