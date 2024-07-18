package com.hh.consertreservation.integration;

import com.hh.consertreservation.application.facade.CashFacade;
import com.hh.consertreservation.application.facade.ConcertFacade;
import com.hh.consertreservation.domain.cash.CashService;
import com.hh.consertreservation.domain.concert.ConcertService;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.concert.SeatRepository;
import com.hh.consertreservation.domain.concert.SeatType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@SpringBootTest
public class ConcertIntegrationTest {


    @Autowired
    ConcertFacade concertFacade;

    @Autowired
    SeatRepository seatRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    @Test
    void 좌석예약_동시성테스트() throws Exception {
        long scheduleId = 1L;
        long seatNumber = 8L;

        CompletableFuture<Optional<Seat>> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                return concertFacade.reservation(scheduleId, seatNumber);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return Optional.empty();
            }
        });
        CompletableFuture<Optional<Seat>> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                return concertFacade.reservation(scheduleId, seatNumber);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return Optional.empty();
            }
        });
        CompletableFuture.allOf(future1, future2).join();

        Optional<Seat> seat1 = future1.get();
        Optional<Seat> seat2 = future2.get();

        //둘중 하나만 데이터가 있어야함
        if (seat1.isPresent()) {
            Assertions.assertEquals(seat2, Optional.empty());
        }
        if (seat2.isPresent()) {
            Assertions.assertEquals(seat1, Optional.empty());
        }
    }
}
