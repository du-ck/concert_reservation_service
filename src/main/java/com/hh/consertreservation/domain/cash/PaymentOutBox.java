package com.hh.consertreservation.domain.cash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
public class PaymentOutBox {
    private Long id;
    private Long userId;
    private String message;
    private PaymentOutBoxType status;
    private PaymentOutBoxProcessType processStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PaymentOutBox publish(PaymentOutBox item) {
         return item.toBuilder()
                .status(PaymentOutBoxType.PUBLISHED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentOutBox processReceive(PaymentOutBox item) {
        return item.toBuilder()
                .processStatus(PaymentOutBoxProcessType.RECEIVED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentOutBox processSuccess(PaymentOutBox item) {
        return item.toBuilder()
                .processStatus(PaymentOutBoxProcessType.SUCCESS)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
