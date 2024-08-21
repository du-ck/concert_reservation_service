package com.hh.consertreservation.interfaces.api.concert;

import com.hh.consertreservation.domain.concert.Seat;
import lombok.*;

import java.util.List;

public class ConcertSeats {
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long userId;
        private Long concertId;
        private String concertDateTime;
    }

    @Builder
    @Getter
    public static class Response {
        List<Seat> seats;
    }
}
