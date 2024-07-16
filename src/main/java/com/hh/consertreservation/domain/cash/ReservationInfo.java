package com.hh.consertreservation.domain.cash;

import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
public class ReservationInfo {
    private Long reservationId;
    private User user;
    private ConcertSchedule concertSchedule;
    private Seat seat;
    private LocalDateTime createdAt;
    private ReservationType status;

    public void setMockData() {
        this.reservationId = 1L;
        Seat seat1 = Seat.builder().build();
        seat1.setMockData();
        this.seat = seat1;
        this.createdAt = LocalDateTime.now();
        this.status = ReservationType.RESERVED;
    }
}
