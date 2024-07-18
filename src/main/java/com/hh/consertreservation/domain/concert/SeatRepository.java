package com.hh.consertreservation.domain.concert;


import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    List<Seat> getSeats(long scheduleId);

    Optional<Seat> getSeatForReservation(long scheduleId, long seatNumber);

    Optional<Seat> save(Seat seat);

    void setSeatStatusEmpty();
    Optional<Seat> findByIdWithTemp(long seatId);
    Optional<Seat> setSeatReserved(long seatId);
}
