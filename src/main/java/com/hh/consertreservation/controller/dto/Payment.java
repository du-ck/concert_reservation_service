package com.hh.consertreservation.controller.dto;

import com.hh.consertreservation.domain.dto.ReservationInfo;
import com.hh.consertreservation.domain.dto.User;
import com.hh.consertreservation.application.facade.dto.PaymentFacadeRequestDto;
import lombok.*;

public class Payment {
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long userId;
        private Long scheduleId;
        private Long seatId;
    }

    @Builder
    @Getter
    public static class Response {
        ReservationInfo reservation;
    }

    public static PaymentFacadeRequestDto toServiceRequestDto(Payment.Request dto, String token) {
        return PaymentFacadeRequestDto.builder()
                .userId(dto.getUserId())
                .seatId(dto.getSeatId())
                .scheduleId(dto.getScheduleId())
                .token(token)
                .build();
    }
}
