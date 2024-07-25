package com.hh.consertreservation.application.facade;

import com.hh.consertreservation.domain.cash.ReservationInfo;
import com.hh.consertreservation.domain.cash.UserBalance;
import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.application.facade.dto.PaymentFacadeRequestDto;
import com.hh.consertreservation.domain.cash.CashService;
import com.hh.consertreservation.domain.concert.ConcertService;
import com.hh.consertreservation.domain.user.User;
import com.hh.consertreservation.domain.user.UserService;
import com.hh.consertreservation.domain.waiting.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CashFacade {
    private final CashService cashService;
    private final ConcertService concertService;
    private final UserService userService;
    private final WaitingService waitingService;

    public Optional<UserBalance> getUserBalance(long userId) throws Exception {
        return cashService.getUserBalance(userId);
    }



    public Optional<UserBalance> charge(long userId, long amount) throws Exception {
        log.info("[쓰레드ID : {}] 파사드 시작!!",Thread.currentThread().getId());
        Optional<UserBalance> balance = cashService.charge(userId, amount);
        log.info("[쓰레드ID : {}] 파사드 끝!!",Thread.currentThread().getId());
        return balance;
    }

    @Transactional
    public Optional<ReservationInfo> payment(PaymentFacadeRequestDto req) throws Exception {
        log.info("[쓰레드ID : {}] [paymentFacade] 파사드 시작!!",Thread.currentThread().getId());

        Optional<User> user = userService.getUserWithLock(req.getUserId());
        log.info("[쓰레드ID : {}] [paymentFacade] 결제 락 획득!!",Thread.currentThread().getId());

        Optional<Seat> seat = concertService.getSeatWithId(req.getSeatId());
        Optional<ConcertSchedule> schedule = concertService.getScheduleWithId(req.getScheduleId());


        Optional<ReservationInfo> reservation = cashService.payment(user.get(), seat.get(), schedule.get());
        log.info("[쓰레드ID : {}] [paymentFacade] 결제 처리!!",Thread.currentThread().getId());
        
        if (reservation.isPresent()) {
            //save 완료일 경우
            // 1. 좌석 임시배정 -> 배정
            // 2. user 잔액 차감 (cashService.payment() 에서 진행)
            // 3. 토큰 만료
            Optional<Seat> reservedSeat = concertService.setReservedSeat(req.getSeatId());
            log.info("[쓰레드ID : {}] [paymentFacade] 좌석 배정 처리!!",Thread.currentThread().getId());
            int expireTokenCount = waitingService.expireAfterPayment(req.getUserId(), req.getToken());
            log.info("[쓰레드ID : {}] [paymentFacade] 파사드 끝!!",Thread.currentThread().getId());
            return reservation;
        }
        log.info("[쓰레드ID : {}] [paymentFacade] 파사드 끝!!",Thread.currentThread().getId());
        return Optional.empty();
    }
}
