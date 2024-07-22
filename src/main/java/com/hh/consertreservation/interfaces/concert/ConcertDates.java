package com.hh.consertreservation.interfaces.concert;

import com.hh.consertreservation.domain.concert.Concert;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

public class ConcertDates {
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Jacksonized
    public static class Request {
        private Long concertId;
    }

    @Builder
    @Getter
    public static class Response {
        Concert concerts;
    }
}
