package com.hh.consertreservation.application.facade.dto;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class PaymentFacadeRequestDto {
    private Long userId;
    private Long scheduleId;
    private Long seatId;
    private String token;
}
