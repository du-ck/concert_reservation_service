package com.hh.consertreservation.application.scheduler;

import com.hh.consertreservation.domain.service.ConcertService;
import com.hh.consertreservation.domain.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SeatScheduler {

    private final ConcertService concertService;

    /**
     * 임시배정 5분 지난 좌석들 초기화하는 스케쥴러
     */
    @Scheduled(fixedRate = 2000)
    public void tokenExpire() {
        concertService.setEmptySeat();
    }
}
