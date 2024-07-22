package com.hh.consertreservation.interfaces.dto;

public record ErrorResponse(
        boolean isSuccess,
        String code,
        String message
) {
}
