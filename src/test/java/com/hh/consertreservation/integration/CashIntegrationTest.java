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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
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
        long amount = 1000L;
        int numThreads = 10;    //쓰레드 개수
        Optional<UserBalance> beforeBalance = cashFacade.getUserBalance(userId);
        System.out.println("before Balance : " + beforeBalance.get().getBalance());

        CountDownLatch latch = new CountDownLatch(numThreads);  //쓰레드들을 동시 시작 및 종료를 관리하기 위한 객체
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads); //정해진 쓰레드들(numThreads)에게 동시에 작업할당을 하기위한 객체

        List<String> exMsg = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    Exception exception = Assertions.assertThrows(Exception.class,
                            () -> cashFacade.charge(userId, amount));
                    exMsg.add(exception.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();  // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown(); //쓰레드 풀 종료

        exMsg.forEach(msg -> System.out.println("exception Message :: " + msg));

        Optional<UserBalance> balance = cashFacade.getUserBalance(userId);
        System.out.println("after Balance : " + balance.get().getBalance());
        Assertions.assertEquals(balance.get().getBalance(), beforeBalance.get().getBalance() + amount * numThreads);
    }

    @Test
    void 결제_동시성테스트() throws Exception {
        long seatId = 5L;
        long scheduleId = 1L;
        Optional<Seat> tempSeat = concertFacade.reservation(scheduleId, seatId);
        Optional<UserBalance> beforeBalance = cashFacade.getUserBalance(userId);
        //테스트를 위한 토큰 발급
        Optional<Token> token = tokenFacade.issued(userId, 500L);
        String queueToken = token.get().getQueueToken();

        PaymentFacadeRequestDto req = PaymentFacadeRequestDto.builder()
                .userId(userId)
                .scheduleId(scheduleId)
                .token(queueToken)
                .seatId(seatId)
                .build();

        int numThreads = 10;    //쓰레드 개수
        CountDownLatch latch = new CountDownLatch(numThreads);  //쓰레드들을 동시 시작 및 종료를 관리하기 위한 객체
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads); //정해진 쓰레드들(numThreads)에게 동시에 작업할당을 하기위한 객체
        List<String> exMsg = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    Exception exception = Assertions.assertThrows(Exception.class,
                            () -> cashFacade.payment(req));
                    exMsg.add(exception.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();  // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown(); //쓰레드 풀 종료

        exMsg.forEach(msg -> System.out.println("exception Message :: " + msg));

        ReservationInfo result = reservationRepository.findByUserIdAndScheduleId(userId, scheduleId).get();
        Optional<UserBalance> afterBalance = cashFacade.getUserBalance(userId);

        //토큰은 만료되야함
        Exception tokenException = Assertions.assertThrows(TokenVerificationException.class,
                () -> tokenFacade.verification(userId, queueToken));
        Assertions.assertEquals("토큰인증 실패", tokenException.getMessage());

        Assertions.assertEquals(result.getSeat().getId(), seatId);
        Assertions.assertEquals(result.getUser().getId(), userId);
        // numThreads 중 한개는 성공하고 나머지는 실패해야한다 (exMsg 리스트에 쌓임)
        Assertions.assertEquals(exMsg.size(), numThreads - 1);
    }
}
