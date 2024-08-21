package com.hh.consertreservation.application.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.consertreservation.domain.cash.*;
import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.application.facade.dto.PaymentFacadeRequestDto;
import com.hh.consertreservation.domain.concert.ConcertService;
import com.hh.consertreservation.domain.user.User;
import com.hh.consertreservation.domain.user.UserService;
import com.hh.consertreservation.domain.waiting.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class CashFacade {
    private final CashService cashService;
    private final ConcertService concertService;
    private final UserService userService;
    private final WaitingService waitingService;
    private final ObjectMapper objectMapper;

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

    public void sendKafkaMessage(String message) {
        cashService.sendKafkaMessage(message);
    }

    /**
     * 결제 시 포인트를 차감하는 로직
     *  포인트 차감후 event 를 발행시켜서
     */
    @Transactional
    public boolean minutPointForPayment(PaymentFacadeRequestDto req) throws Exception {
        log.debug("[Logic Check][minutPointForPayment] 1. 로직 시작");
        Optional<User> user = userService.getUserWithLock(req.getUserId());
        Optional<ConcertSchedule> schedule = concertService.getScheduleWithId(req.getScheduleId());
        //포인트 차감
        Optional<UserBalance> balance = cashService.minutPointForPayment(user.get(), schedule.get().getPrice());
        if (balance.isPresent()) {
            //outbox 저장
            PaymentOutBox outBox = PaymentOutBox.builder()
                    .userId(user.get().getId())
                    .message(objectMapper.writeValueAsString(req))
                    .status(PaymentOutBoxType.INIT)
                    .processStatus(PaymentOutBoxProcessType.NONE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            cashService.savePaymentOutBox(outBox);
            log.debug("[Logic Check][minutPointForPayment] 2. PaymentOutBox Init");
            //event publish
            publisher.publishEvent(req);
            log.debug("[Logic Check][minutPointForPayment] 3. event publish");
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<ReservationInfo> paymentWithKafka(String message, boolean isScheduler) throws Exception {
        log.debug("[Logic Check][paymentWithKafka] 7-2. 결제 처리");
        PaymentFacadeRequestDto req =  objectMapper.readValue(message, PaymentFacadeRequestDto.class);

        Optional<User> user = userService.getUserWithLock(req.getUserId());
        Optional<Seat> seat = concertService.getSeatWithId(req.getSeatId());
        Optional<ConcertSchedule> schedule = concertService.getScheduleWithId(req.getScheduleId());

        Optional<ReservationInfo> reservationInfo = cashService.paymentWithKafka(user.get(), seat.get(), schedule.get(), message);

        if (reservationInfo.isPresent()) {
            Optional<Seat> reservedSeat = concertService.setReservedSeat(req.getSeatId());
            log.debug("[Logic Check][paymentWithKafka] 8. 결제 처리 완료");
            if (!isScheduler) {
                Long expireTokenCount = waitingService.expireAfterPayment(req.getToken());
                log.debug("[Logic Check][paymentWithKafka] 9. 토큰만료 처리 완료");
            }
            return reservationInfo;
        }
        return Optional.empty();
    }

    public Optional<PaymentOutBox> publishPaymentoutBox(String message) {

        Optional<PaymentOutBox> outbox = cashService.findByMessage(message);
        //publish 처리
        PaymentOutBox publishOutBox = PaymentOutBox.publish(outbox.get());
        Optional<PaymentOutBox> saveOutBox = cashService.savePaymentOutBox(publishOutBox);
        if (saveOutBox.isPresent()) {
            log.debug("[Logic Check][publishPaymentoutBox] 7-1. paymentoutbox Published 처리");
            return saveOutBox;
        }
        return Optional.empty();
    }

    /**
     * 스케쥴러에서 사용하는 메서드
     * outbox 테이블에서 init 상태인 아이템들을
     * 재전송하는 로직
     */
    @Transactional
    public void resendKafka() {
        //outbox 에서 init item 들 조회
        List<PaymentOutBox> initItems = cashService.findAllByInit();

        if (!CollectionUtils.isEmpty(initItems)) {
            initItems.forEach(item -> {
                //kafka에 재전송 (3분이 지난 데이터만)
                if (LocalDateTime.now().isAfter(item.getCreatedAt().plusMinutes(1))) {
                    if (StringUtils.hasText(item.getMessage())) {
                        try {
                            this.paymentWithKafka(item.getMessage(), true);
                            log.info("[resendKafka] 처리!!");
                            publishPaymentoutBox(item.getMessage());
                            log.info("[resendKafka] published 처리!!");
                        } catch (Exception e) {

                        }
                    }
                }
            });
        }
    }
}
