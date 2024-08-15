package com.hh.consertreservation.domain.cash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.user.User;
import com.hh.consertreservation.support.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashService {

    private final UserBalanceRepository userBalanceRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentKafkaPublisher paymentKafkaPublisher;
    private final PaymentOutBoxRepository paymentOutBoxRepository;
    private final ObjectMapper objectMapper;

    public Optional<UserBalance> getUserBalance(long userId) throws Exception {
        Optional<UserBalance> result = userBalanceRepository.findByUserId(userId);
        if (!result.isPresent()) {
            throw new ResourceNotFoundException("유저를 찾을 수 없습니다");
        }
        return result;
    }


    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Optional<UserBalance> charge(long userId, long amount) throws Exception {

        Optional<UserBalance> result = userBalanceRepository.findByUserIdWithLock(userId);

        if (result.isPresent()) {
            UserBalance userBalance = result.get();
            userBalance.charge(amount);

            Optional<UserBalance> balance = userBalanceRepository.save(userBalance);
            if (balance.isPresent()) {
                return balance;
            }
        }
        return Optional.empty();
    }


    public Optional<ReservationInfo> payment(User user, Seat seat, ConcertSchedule schedule) {
        
        List<ReservationInfo> reservationInfo = reservationRepository.findByUserIdAndScheduleId(user.getId(), schedule.getScheduleId());
        if (!CollectionUtils.isEmpty(reservationInfo)) {
            throw new IllegalArgumentException("이미 예약한 일정입니다");
        }

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

    public Optional<ReservationInfo> paymentWithKafka(User user, Seat seat, ConcertSchedule schedule, String message) {
        List<ReservationInfo> reservationInfo = reservationRepository.findByUserIdAndScheduleId(user.getId(), schedule.getScheduleId());
        if (!CollectionUtils.isEmpty(reservationInfo)) {
            throw new IllegalArgumentException("이미 예약한 일정입니다");
        }
        //결제 처리
        seat.paymentSeat();
        ReservationInfo reservation = ReservationInfo.builder()
                .concertSchedule(schedule)
                .seat(seat)
                .user(user)
                .createdAt(LocalDateTime.now())
                .status(ReservationType.RESERVED)
                .build();

        Optional<PaymentOutBox> findOutBox = findByMessage(message);
        if (findOutBox.isPresent()) {

            //outbox process status = received
            PaymentOutBox outBox = findOutBox.get();
            Optional<PaymentOutBox> receivedOutBox = paymentOutBoxRepository.save(PaymentOutBox.processReceive(outBox));

            //reservation 정보 저장
            Optional<ReservationInfo> saveResult = reservationRepository.save(reservation);
            if (receivedOutBox.isPresent() && saveResult.isPresent()) {
                //outbox process status = success
                paymentOutBoxRepository.save(PaymentOutBox.processSuccess(receivedOutBox.get()));

                ReservationInfo result = reservation.toBuilder()
                        .reservationId(saveResult.get().getReservationId())
                        .build();
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    public Optional<UserBalance> minutPointForPayment(User user, long price) {
        user.getBalance().payment(price);
        return userBalanceRepository.save(user.getBalance());
    }

    /**
     * EventPublisher 를 통해 카프카에 전송하는 메서드
     */
    public void sendKafkaMessage(String message) {
        //paymentKafkaPublisher.send(message);
        CompletableFuture<SendResult<String, String>> future = paymentKafkaPublisher.send(message);
        log.debug("[Logic Check][sendKafkaMessage] 5. kafka message 전송완료");
    }

    @Transactional
    public Optional<PaymentOutBox> savePaymentOutBox(PaymentOutBox paymentOutBox) {
        Optional<PaymentOutBox> saveOutBox = paymentOutBoxRepository.save(paymentOutBox);
        return saveOutBox;
    }

    public Optional<PaymentOutBox> findByMessage(String message) {
        return paymentOutBoxRepository.findByMessage(message);
    }

    public List<PaymentOutBox> findAllByInit() {
        return paymentOutBoxRepository.findAllByInit();
    }

    public void sendKafkaMessageWithTopic(String topic, String message) {
        CompletableFuture<SendResult<String, String>> future = paymentKafkaPublisher.sendWithTopic(topic, message);
    }
}
