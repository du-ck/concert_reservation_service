package com.hh.consertreservation.interfaces.api.scheduler;

import com.hh.consertreservation.domain.waiting.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WaitingScheduler {

    private final WaitingService waitingService;

    public final static Long MAXIMUM_ONGOING_COUNT = 3000L;  // 가용가능한 인원
    public final static int ACTIVE_COUNT = 50;  // 한번에 active 시킬 토큰의 개수


    /*@Scheduled(fixedRate = 10000)
    public void tokenExpire() {
        waitingService.expireWithRedis();
    }*/

    @Scheduled(fixedRate = 10000)
    public void active() {
        waitingService.active(ACTIVE_COUNT);
    }
}
