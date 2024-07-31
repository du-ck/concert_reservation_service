package com.hh.consertreservation.integration;

import com.hh.consertreservation.application.facade.CashFacade;
import com.hh.consertreservation.application.facade.ConcertFacade;
import com.hh.consertreservation.application.facade.TokenFacade;
import com.hh.consertreservation.application.facade.dto.PaymentFacadeRequestDto;
import com.hh.consertreservation.domain.cash.ReservationInfo;
import com.hh.consertreservation.domain.cash.ReservationRepository;
import com.hh.consertreservation.domain.cash.UserBalance;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.waiting.Token;
import com.hh.consertreservation.support.exception.TokenVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@Slf4j
public class CashIntegrationTest {

    @Autowired
    CashFacade cashFacade;

    @Autowired
    TokenFacade tokenFacade;

    @Autowired
    ConcertFacade concertFacade;

    @Autowired
    ReservationRepository reservationRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        reservationRepository.deleteAll();
    }

    @Test
    void 충전_동시성테스트() throws Exception {
        long amount = 100L;
        int numThreads = 1000;    //쓰레드 개수
        Optional<UserBalance> beforeBalance = cashFacade.getUserBalance(userId);

        CountDownLatch latch = new CountDownLatch(numThreads);  //쓰레드들을 동시 시작 및 종료를 관리하기 위한 객체
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads); //정해진 쓰레드들(numThreads)에게 동시에 작업할당을 하기위한 객체

        Long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    cashFacade.charge(userId, amount);
                } catch (ObjectOptimisticLockingFailureException ex) {
                    log.error("[쓰레드ID : {}] ObjectOptimisticLockingFailureException :: {}", Thread.currentThread().getId(), ex.getMessage());
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

        Optional<UserBalance> balance = cashFacade.getUserBalance(userId);
        log.info("before Balance : {}", beforeBalance.get().getBalance());
        log.info("충전한 포인트 : {} 원씩 {} 번 = {}", amount, numThreads, amount * numThreads);
        log.info("after Balance : {}", balance.get().getBalance());
        Assertions.assertEquals(balance.get().getBalance(), beforeBalance.get().getBalance() + amount);
    }

    @Test
    void 결제_동시성테스트() throws Exception {
        Thread.sleep(1000); //BeforeEach 도는 시간 기다림

        long seatId = 9L;
        long scheduleId = 1L;
        Optional<Seat> tempSeat = concertFacade.reserve(scheduleId, seatId);
        Optional<UserBalance> beforeBalance = cashFacade.getUserBalance(userId);
        //테스트를 위한 토큰 발급
        String token = tokenFacade.issued(userId, 500L);

        PaymentFacadeRequestDto req = PaymentFacadeRequestDto.builder()
                .userId(userId)
                .scheduleId(scheduleId)
                .token(token)
                .seatId(seatId)
                .build();

        int numThreads = 5;    //쓰레드 개수
        CountDownLatch latch = new CountDownLatch(numThreads);  //쓰레드들을 동시 시작 및 종료를 관리하기 위한 객체
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads); //정해진 쓰레드들(numThreads)에게 동시에 작업할당을 하기위한 객체

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    cashFacade.payment(req);
                } catch (ObjectOptimisticLockingFailureException ex) {
                    log.error("[쓰레드ID : {}] ObjectOptimisticLockingFailureException :: {}", Thread.currentThread().getId(), ex.getMessage());
                } catch (Exception ex) {
                    log.error("[쓰레드ID : {}] Exception :: {}", Thread.currentThread().getId(), ex.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();  // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown(); //쓰레드 풀 종료

        List<ReservationInfo> result = reservationRepository.findByUserIdAndScheduleId(userId, scheduleId);
        Optional<UserBalance> afterBalance = cashFacade.getUserBalance(userId);

        //토큰은 만료되야함
        Exception tokenException = Assertions.assertThrows(TokenVerificationException.class,
                () -> tokenFacade.verification(token));
        Assertions.assertEquals("토큰인증 실패", tokenException.getMessage());
    }
}
