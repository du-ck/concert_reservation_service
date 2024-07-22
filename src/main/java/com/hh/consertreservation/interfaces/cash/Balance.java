package com.hh.consertreservation.interfaces.cash;

import com.hh.consertreservation.domain.cash.UserBalance;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

public class Balance {
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Jacksonized
    public static class Request {
        private Long userId;
    }

    @Builder
    @Getter
    public static class Response {
        private UserBalance balance;
    }
}
