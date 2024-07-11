package com.hh.consertreservation.controller.dto;

import com.hh.consertreservation.domain.dto.Seat;
import lombok.*;

public class Reservation {
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long scheduleId;
        private Long userId;
        private Long seatNumber;
    }

    @Builder
    @Getter
    public static class Response {
        Seat seat;  //예약요청한 좌석 (임시배정)
    }
}
