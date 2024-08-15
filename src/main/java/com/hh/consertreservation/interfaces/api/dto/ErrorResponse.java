package com.hh.consertreservation.interfaces.api.dto;

public record ErrorResponse(
        boolean isSuccess,
        String code,
        String message
) {
}
