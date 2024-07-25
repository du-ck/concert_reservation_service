package com.hh.consertreservation.domain.cash;

import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.user.User;
import com.hh.consertreservation.support.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
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


    @Transactional
    public Optional<UserBalance> charge(long userId, long amount) throws Exception {
        log.info("[쓰레드ID : {}] 서비스 시작!!",Thread.currentThread().getId());

        Optional<UserBalance> result = userBalanceRepository.findByUserIdWithLock(userId);
        log.info("[쓰레드ID : {}] 충전 락 획득!!",Thread.currentThread().getId());

        if (result.isPresent()) {
            UserBalance userBalance = result.get();
            userBalance.charge(amount);

            Optional<UserBalance> balance = userBalanceRepository.save(userBalance);
            if (balance.isPresent()) {
                log.info("[쓰레드ID : {}] 충전 완료!!",Thread.currentThread().getId());
                log.info("[쓰레드ID : {}] 서비스 끝!!",Thread.currentThread().getId());
                return balance;
            }
        }
        log.info("[쓰레드ID : {}] 서비스 끝!!",Thread.currentThread().getId());
        return Optional.empty();
    }


    public Optional<ReservationInfo> payment(User user, Seat seat, ConcertSchedule schedule) {
        log.info("[쓰레드ID : {}] [paymentService] 서비스 시작!!",Thread.currentThread().getId());
        
        List<ReservationInfo> reservationInfo = reservationRepository.findByUserIdAndScheduleId(user.getId(), schedule.getScheduleId());
        log.info("[쓰레드ID : {}] [paymentService] 이미 예약한 일정인지 조회!!",Thread.currentThread().getId());
        if (!CollectionUtils.isEmpty(reservationInfo)) {
            throw new IllegalArgumentException("이미 예약한 일정입니다");
        }

        //결제 처리
        seat.paymentSeat();
        user.getBalance().payment(schedule.getPrice());
        log.info("[쓰레드ID : {}] [paymentService] 결제 처리!!",Thread.currentThread().getId());
        //결제처리된 잔액으로 저장
        Optional<UserBalance> balance = userBalanceRepository.save(user.getBalance());
        log.info("[쓰레드ID : {}] [paymentService] 결제 처리정보 저장!!",Thread.currentThread().getId());

        ReservationInfo reservation = ReservationInfo.builder()
                .concertSchedule(schedule)
                .seat(seat)
                .user(user)
                .createdAt(LocalDateTime.now())
                .status(ReservationType.RESERVED)
                .build();

        Optional<ReservationInfo> saveResult = reservationRepository.save(reservation);
        log.info("[쓰레드ID : {}] [paymentService] 예약정보 저장!!",Thread.currentThread().getId());
        //saveResult 있다면 save 된것이므로 많은 데이터가 담겨있는 reservation 를 return 시킨다.
        if (saveResult.isPresent()) {
            ReservationInfo result = saveResult.get();
            result = reservation.toBuilder()
                    .reservationId(saveResult.get().getReservationId())
                    .build();
            log.info("[쓰레드ID : {}] [paymentService] 서비스 끝!!",Thread.currentThread().getId());
            return Optional.of(result);
        }
        log.info("[쓰레드ID : {}] [paymentService] 서비스 끝!!",Thread.currentThread().getId());
        return Optional.empty();
    }
}
