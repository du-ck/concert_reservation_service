package com.hh.consertreservation.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
public class UserBalance {
    private Long id;
    private Long userId;
    private Long balance;
    private LocalDateTime updatedAt;


    public void setMockData() {
        this.userId = 1L;
        this.balance = 10000L;
        this.updatedAt = LocalDateTime.now();
    }

    public void charge(long amount) {
        if (amount <= 0) {
            //충전할 금액이 0 이하일수 없다
            throw new IllegalArgumentException("충전할 금액은 0보다 커야 합니다");
        }
        this.balance += amount;
    }
}
