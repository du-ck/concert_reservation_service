package com.hh.consertreservation.interfaces.event.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.consertreservation.application.facade.CashFacade;
import com.hh.consertreservation.application.facade.dto.PaymentFacadeRequestDto;
import com.hh.consertreservation.domain.cash.ReservationInfo;
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

    //private final PaymentMockApiClient mockApiClient;
    private final CashFacade cashFacade;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPaymentInfo(PaymentFacadeRequestDto req) {
        try {
            //mockApiClient.sendPaymentInfo(reservationInfo);
            log.debug("[Logic Check][sendPaymentInfo] 4. kafka message 전송");
            cashFacade.sendKafkaMessage(objectMapper.writeValueAsString(req));
        } catch (Exception e) {
            log.error("전송 실패");
        }
    }
}
