package com.hh.consertreservation.domain.cash;


import java.util.Optional;

public interface ReservationRepository {
    Optional<ReservationInfo> save(ReservationInfo reservationInfo);
    Optional<ReservationInfo> findByUserIdAndScheduleId(long userId, long scheduleId);
    void deleteAll();
}
