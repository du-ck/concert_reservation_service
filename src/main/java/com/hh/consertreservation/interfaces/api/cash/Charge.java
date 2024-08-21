package com.hh.consertreservation.interfaces.api.cash;

import com.hh.consertreservation.domain.cash.UserBalance;
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
