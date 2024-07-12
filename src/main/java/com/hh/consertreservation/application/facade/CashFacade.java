package com.hh.consertreservation.application.facade;

import com.hh.consertreservation.domain.dto.*;
import com.hh.consertreservation.application.facade.dto.PaymentFacadeRequestDto;
import com.hh.consertreservation.domain.service.CashService;
import com.hh.consertreservation.domain.service.ConcertService;
import com.hh.consertreservation.domain.service.UserService;
import com.hh.consertreservation.domain.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CashFacade {
    private final CashService cashService;
    private final ConcertService concertService;
    private final UserService userService;
    private final WaitingService waitingService;

    public Optional<UserBalance> getUserBalance(long userId) throws Exception {
        return cashService.getUserBalance(userId);
    }


    @Transactional
    public Optional<UserBalance> charge(long userId, long amount) throws Exception {
        return cashService.charge(userId, amount);
    }

    @Transactional
    public Optional<ReservationInfo> payment(PaymentFacadeRequestDto req) throws Exception {
        Optional<User> user = userService.getUser(req.getUserId());
        Optional<Seat> seat = concertService.getSeatWithId(req.getSeatId());
        Optional<ConcertSchedule> schedule = concertService.getScheduleWithId(req.getScheduleId());


        Optional<ReservationInfo> reservation = cashService.payment(user.get(), seat.get(), schedule.get());
        if (reservation.isPresent()) {
            //save 완료일 경우
            // 1. 좌석 임시배정 -> 배정
            // 2. user 잔액 차감 (cashService.payment() 에서 진행)
            // 3. 토큰 만료
            Optional<Seat> reservedSeat = concertService.setReservedSeat(req.getSeatId());

            int expireTokenCount = waitingService.expireAfterPayment(req.getUserId(), req.getToken());

            return reservation;
        }
        return Optional.empty();
    }
}
