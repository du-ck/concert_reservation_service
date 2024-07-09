package com.hh.consertreservation.controller.dto;

import com.hh.consertreservation.domain.dto.ReservationInfo;
import com.hh.consertreservation.domain.dto.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Payment {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long userId;
        private Long concertId;
        private Long seatNo;
        private String concertDateTime;
    }

    @Builder
    @Getter
    public static class Response {
        User user;
        ReservationInfo reservation;
    }
}
