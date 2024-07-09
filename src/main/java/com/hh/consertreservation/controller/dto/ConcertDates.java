package com.hh.consertreservation.controller.dto;

import com.hh.consertreservation.domain.dto.Concert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ConcertDates {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long concertId;
    }

    @Builder
    @Getter
    public static class Response {
        List<Concert> concerts;
    }
}
