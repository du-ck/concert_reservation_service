package com.hh.consertreservation.controller.dto;

import com.hh.consertreservation.domain.dto.ReservationInfo;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.dto.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Reservation {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long concertId;
        private Long userId;
        private String concertDateTime;
        private Long seatNo;
    }

    @Builder
    @Getter
    public static class Response {
        Seat seat;  //예약요청한 좌석 (임시배정)
        boolean result;     //임시배정 성공 여부
    }
}
