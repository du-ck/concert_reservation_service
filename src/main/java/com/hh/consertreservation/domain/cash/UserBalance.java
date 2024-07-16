package com.hh.consertreservation.domain.cash;

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
        this.balance = 50000L;
        this.updatedAt = LocalDateTime.now();
    }

    public void charge(long amount) {
        if (amount <= 0) {
            //충전할 금액이 0 이하일수 없다
            throw new IllegalArgumentException("충전할 금액은 0보다 커야 합니다");
        }
        this.balance += amount;
    }

    public void payment(long amount) {
        if (amount <= 0) {
            //사용할 금액이 0 이하일수 없다
            throw new IllegalArgumentException("사용할 금액은 0보다 커야 합니다");
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException("잔액 부족");
        }
        this.balance -= amount;
    }
}
