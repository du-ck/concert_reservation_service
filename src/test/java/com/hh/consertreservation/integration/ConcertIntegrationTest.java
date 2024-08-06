package com.hh.consertreservation.integration;

import com.hh.consertreservation.application.facade.ConcertFacade;
import com.hh.consertreservation.domain.concert.ConcertService;
import com.hh.consertreservation.domain.concert.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.*;

@SpringBootTest
@Slf4j
public class ConcertIntegrationTest {


    @Autowired
    ConcertFacade concertFacade;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    ConcertService concertService;

    private Long userId;
    private Long scheduleId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        scheduleId = 1L;
        seatRepository.setSeatAllEmptyForTest(scheduleId);
    }

    @Test
    void 좌석예약_동시성테스트() throws Exception {
        Thread.sleep(1000); //BeforeEach 도는 시간 기다림
        int numThreads = 100;    //쓰레드 개수
        long seatNumber = 6L;

        CountDownLatch latch = new CountDownLatch(numThreads);  //쓰레드들을 동시 시작 및 종료를 관리하기 위한 객체
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        Long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    concertFacade.reserve(scheduleId, seatNumber);
                } catch (ObjectOptimisticLockingFailureException e) {
                    log.error("[쓰레드ID : {}] ObjectOptimisticLockingFailureException :: {}", Thread.currentThread().getId() , e.getMessage());
                } catch (Exception ex) {
                    log.error("[쓰레드ID : {}] Exception :: {}", Thread.currentThread().getId(), ex.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();  // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown(); //쓰레드 풀 종료

        Long endTime = System.currentTimeMillis();
        log.info("소요 시간: {}", (endTime - startTime) + "ms");
    }

    @Test
    void 날짜조회_캐싱처리_속도비교() throws Exception {

        //캐싱을 위해 호출
        concertService.getDates(1L);
        Thread.sleep(1000);
        //캐싱된걸 호출
        log.info("캐싱된 목록 조회 시작!");
        Long startTime = System.currentTimeMillis();
        concertService.getDates(1L);
        Long endTime = System.currentTimeMillis();
        log.info("캐싱된 목록 조회 끝!");
        log.info("캐싱된 목록 조회 소요 시간: {}", (endTime - startTime) + "ms");

        log.info("캐싱없는 목록 조회 시작!");
        startTime = System.currentTimeMillis();
        concertService.getDatesNoCacheForTest(1L);
        endTime = System.currentTimeMillis();
        log.info("캐싱없는 목록 조회 끝!");
        log.info("캐싱없는 목록 조회 소요 시간: {}", (endTime - startTime) + "ms");
    }

    @Test
    void 콘서트조회_캐싱처리_속도비교() throws Exception {
        //캐싱을 위해 호출
        concertService.getConcerts();
        Thread.sleep(1000);

        //캐싱된걸 호출
        log.info("캐싱된 목록 조회 시작!");
        Long startTime = System.currentTimeMillis();
        concertService.getConcerts();
        Long endTime = System.currentTimeMillis();
        log.info("캐싱된 목록 조회 끝!");
        log.info("캐싱된 목록 조회 소요 시간: {}", (endTime - startTime) + "ms");

        log.info("캐싱없는 목록 조회 시작!");
        startTime = System.currentTimeMillis();
        concertService.getConcertsNoCacheForTest();
        endTime = System.currentTimeMillis();
        log.info("캐싱없는 목록 조회 끝!");
        log.info("캐싱없는 목록 조회 소요 시간: {}", (endTime - startTime) + "ms");
    }
}
