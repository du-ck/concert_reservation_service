package com.hh.consertreservation.domain.waiting;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * redis 에서 ActiveToken 을 관리하기 위한 클래스
 */
@Builder(toBuilder = true)
@Getter
public class ActiveToken {

    private String uuid;
    private Long userId;

    public void issued() {
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
    }

    public void issued(String uuid, long userId) {
        this.uuid = uuid;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", uuid, userId);
    }
}
