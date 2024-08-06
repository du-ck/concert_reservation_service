package com.hh.consertreservation.domain.waiting;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * redis 에서 WaitingToken 을 관리하기 위한 클래스
 */
@Builder(toBuilder = true)
@Getter
public class WaitingToken {

    private String token;
    private double requestTime;

    private Long userId;

    public void issue() {
        UUID uuid = UUID.randomUUID();
        this.token = String.format("%s:%s", uuid, userId);
        this.requestTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
