package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.*;
import com.hh.consertreservation.domain.dto.types.ReservationType;
import com.hh.consertreservation.domain.repository.ReservationRepository;
import com.hh.consertreservation.domain.repository.UserBalanceRepository;
import com.hh.consertreservation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashService {

    private final UserBalanceRepository userBalanceRepository;
    private final ReservationRepository reservationRepository;

    public Optional<UserBalance> getUserBalance(long userId) throws Exception {
        Optional<UserBalance> result = userBalanceRepository.findByUserId(userId);
        if (!result.isPresent()) {
            throw new ResourceNotFoundException("유저를 찾을 수 없습니다");
        }
        return result;
    }


    public Optional<UserBalance> charge(long userId, long amount) throws Exception {
        UserBalance userBalance = getUserBalance(userId).get();
        userBalance.charge(amount);

        Optional<UserBalance> balance = userBalanceRepository.save(userBalance);
        if (balance.isPresent()) {
            return balance;
        }
        return Optional.empty();
    }


    public Optional<ReservationInfo> payment(User user, Seat seat, ConcertSchedule schedule) {

        //결제 처리
        seat.paymentSeat();
        user.getBalance().payment(schedule.getPrice());

        //결제처리된 잔액으로 저장
        Optional<UserBalance> balance = userBalanceRepository.save(user.getBalance());

        ReservationInfo reservation = ReservationInfo.builder()
                .concertSchedule(schedule)
                .seat(seat)
                .user(user)
                .createdAt(LocalDateTime.now())
                .status(ReservationType.RESERVED)
                .build();

        Optional<ReservationInfo> saveResult = reservationRepository.save(reservation);
        //saveResult 있다면 save 된것이므로 많은 데이터가 담겨있는 reservation 를 return 시킨다.
        if (saveResult.isPresent()) {
            ReservationInfo result = saveResult.get();
            result = reservation.toBuilder()
                    .reservationId(saveResult.get().getReservationId())
                    .build();
            return Optional.of(result);
        }
        return Optional.empty();
    }
}
