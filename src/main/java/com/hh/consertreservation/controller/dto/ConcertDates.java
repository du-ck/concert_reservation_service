package com.hh.consertreservation.controller.dto;

import com.hh.consertreservation.domain.dto.Concert;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

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
