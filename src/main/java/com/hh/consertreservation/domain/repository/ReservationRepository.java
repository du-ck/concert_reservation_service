package com.hh.consertreservation.domain.repository;


import com.hh.consertreservation.domain.dto.ReservationInfo;

import java.util.Optional;

public interface ReservationRepository {
    Optional<ReservationInfo> save(ReservationInfo reservationInfo);
}
