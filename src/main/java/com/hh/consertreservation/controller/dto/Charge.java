package com.hh.consertreservation.controller.dto;

import com.hh.consertreservation.domain.dto.UserBalance;
import lombok.*;

public class Charge {
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long userId;
        private Long amount;
    }

    @Builder
    @Getter
    public static class Response {
        private UserBalance balance;
    }
}
