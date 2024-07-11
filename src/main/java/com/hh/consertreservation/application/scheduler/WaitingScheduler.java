package com.hh.consertreservation.application.scheduler;

import com.hh.consertreservation.domain.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WaitingScheduler {

    private final WaitingService waitingService;

    public final static Long MAXIMUM_ONGOING_COUNT = 500L;  // 가용가능한 인원

    /**
     * '짐작'하는 다음 대기열1순위의 token Id
     * 처음 동작시 MAXIMUM_ONGOING_COUNT (가용가능인원 ) +1을 따라가며,
     * expire() 가 돌때마다 실제 다음 waiting 1순위의 token Id 를 담는다.
     *
     * 프론트 단 에서는 발급받은 token의 id - nextOnGoingUserId 로 몇명이 앞에 남았는지
     * 짐작이 가능해진다.
     */
    public static Long nextOnGoingTokenId = MAXIMUM_ONGOING_COUNT + 1L;  //

    @Scheduled(fixedRate = 2000)
    public void tokenExpire() {
        nextOnGoingTokenId = waitingService.expire(nextOnGoingTokenId);
        /*log.info("Next OnGoing Expect Token ID : " + nextOnGoingUserId);*/
    }
}
