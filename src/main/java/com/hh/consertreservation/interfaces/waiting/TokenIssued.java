package com.hh.consertreservation.interfaces.waiting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

public class TokenIssued {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Jacksonized    //필드 1개를 request 객체로 받기위해 선언
    public static class Request {
        private Long userId;
    }

    @Builder
    @Getter
    public static class Response {
        private Long userId;
        private String queueToken;
    }
}
