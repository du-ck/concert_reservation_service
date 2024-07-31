package com.hh.consertreservation.domain.waiting;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * redis 에서 WaitingToken 을 관리하기 위한 클래스
 */
@Builder(toBuilder = true)
@Getter
public class WaitingToken {

    private String token;
    private double requestTime;

    private Long userId;

    public void issued() {
        UUID uuid = UUID.randomUUID();
        this.token = String.format("%s:%s", uuid, userId);
        this.requestTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
