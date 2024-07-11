package com.hh.consertreservation.domain.repository;


import com.hh.consertreservation.domain.dto.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    List<Seat> getSeats(long scheduleId);

    Optional<Seat> getSeatForReservation(long scheduleId, long seatNumber);

    Optional<Seat> save(Seat seat);

    void setSeatStatusEmpty();
}
