package com.hh.consertreservation.domain.cash;


import java.util.Optional;

public interface ReservationRepository {
    Optional<ReservationInfo> save(ReservationInfo reservationInfo);
}
