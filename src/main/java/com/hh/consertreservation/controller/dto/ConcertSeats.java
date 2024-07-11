package com.hh.consertreservation.controller.dto;

import com.hh.consertreservation.domain.dto.Seat;
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
