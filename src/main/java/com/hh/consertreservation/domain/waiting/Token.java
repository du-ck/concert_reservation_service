package com.hh.consertreservation.domain.waiting;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
public class Token {
    private Long id;
    private Long userId;
    private String queueToken;
    private WaitingType status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public void setMockData() {
        this.id = 1L;
        this.userId = 1L;
        this.queueToken = "testToken";
        this.status = WaitingType.ONGOING;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
    }
}
