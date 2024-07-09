package com.hh.consertreservation.controller.dto.data;

public record ErrorResponse(
        boolean isSuccess,
        String code,
        String message
) {
}
