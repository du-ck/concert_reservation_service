package com.hh.consertreservation.domain.user;

import com.hh.consertreservation.domain.cash.UserBalance;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class User {
    private Long id;
    private String userName;
    private String phone;
    private String email;
    private UserBalance balance;

    public void setMockData() {
        this.id = 1L;
        this.userName = "짱구";
        this.phone = "010-1234-5678";
        this.email = "Jjang9@example.com";
        UserBalance balance1 = UserBalance.builder().build();
        balance1.setMockData();
        this.balance = balance1;
    }
}
