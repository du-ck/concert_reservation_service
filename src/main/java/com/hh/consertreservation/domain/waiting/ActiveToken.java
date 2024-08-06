package com.hh.consertreservation.domain.waiting;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * redis 에서 ActiveToken 을 관리하기 위한 클래스
 */
@Builder(toBuilder = true)
@Getter
public class ActiveToken {

    private String uuid;
    private Long userId;

    public void issue() {
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
    }

    public void issue(String uuid, long userId) {
        this.uuid = uuid;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", uuid, userId);
    }
}
