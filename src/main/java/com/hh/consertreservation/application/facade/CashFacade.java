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
import org.springframework.context.ApplicationEventPublisher;
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

    private final ApplicationEventPublisher publisher;

    public Optional<UserBalance> getUserBalance(long userId) throws Exception {
        return cashService.getUserBalance(userId);
    }



    public Optional<UserBalance> charge(long userId, long amount) throws Exception {
        Optional<UserBalance> balance = cashService.charge(userId, amount);
        return balance;
    }

    @Transactional
    public Optional<ReservationInfo> payment(PaymentFacadeRequestDto req) throws Exception {

        Optional<User> user = userService.getUserWithLock(req.getUserId());

        Optional<Seat> seat = concertService.getSeatWithId(req.getSeatId());
        Optional<ConcertSchedule> schedule = concertService.getScheduleWithId(req.getScheduleId());
        Optional<ReservationInfo> reservation = cashService.payment(user.get(), seat.get(), schedule.get());
        
        if (reservation.isPresent()) {
            //save 완료일 경우
            // 1. 좌석 임시배정 -> 배정
            // 2. user 잔액 차감 (cashService.payment() 에서 진행)
            // 3. 토큰 만료
            Optional<Seat> reservedSeat = concertService.setReservedSeat(req.getSeatId());
            Long expireTokenCount = waitingService.expireAfterPayment(req.getToken());
            return reservation;
        }
        return Optional.empty();
    }

    @Transactional
    public boolean paymentForSend(PaymentFacadeRequestDto req) throws Exception {
        Optional<User> user = userService.getUserWithLock(req.getUserId());

        Optional<Seat> seat = concertService.getSeatWithId(req.getSeatId());
        Optional<ConcertSchedule> schedule = concertService.getScheduleWithId(req.getScheduleId());
        Optional<ReservationInfo> reservation = cashService.payment(user.get(), seat.get(), schedule.get());

        if (reservation.isPresent()) {
            //save 완료일 경우
            // 1. 좌석 임시배정 -> 배정
            // 2. user 잔액 차감 (cashService.payment() 에서 진행)
            // 3. 토큰 만료
            Optional<Seat> reservedSeat = concertService.setReservedSeat(req.getSeatId());
            Long expireTokenCount = waitingService.expireAfterPayment(req.getToken());

            //event publish
            publisher.publishEvent(reservation);
            return true;
        }
        return false;
    }
}
