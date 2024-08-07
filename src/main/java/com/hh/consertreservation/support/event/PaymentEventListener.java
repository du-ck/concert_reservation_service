package com.hh.consertreservation.support.event;

import com.hh.consertreservation.domain.cash.ReservationInfo;
import com.hh.consertreservation.support.platform.PaymentMockApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentMockApiClient mockApiClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPaymentInfo(ReservationInfo reservationInfo) {
        try {
            mockApiClient.sendPaymentInfo(reservationInfo);
        } catch (Exception e) {
            log.error("전송 실패");
            //보상 트랜잭션 필요
        }
    }
}
