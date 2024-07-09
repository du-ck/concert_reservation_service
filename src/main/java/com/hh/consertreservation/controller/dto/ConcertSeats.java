package com.hh.consertreservation.controller.dto;

import com.hh.consertreservation.domain.dto.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ConcertSeats {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long concertId;
        private String concertDateTime;
    }

    @Builder
    @Getter
    public static class Response {
        List<Seat> seats;
    }
}
