package com.hh.consertreservation.domain.cash;


import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Optional<ReservationInfo> save(ReservationInfo reservationInfo);
    List<ReservationInfo> findByUserIdAndScheduleId(long userId, long scheduleId);
    void deleteAll();
}
