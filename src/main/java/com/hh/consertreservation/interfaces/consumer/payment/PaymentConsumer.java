package com.hh.consertreservation.interfaces.consumer.payment;

import com.hh.consertreservation.application.facade.CashFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

    private final CashFacade cashFacade;

    @KafkaListener(topics = "paymentinfo", groupId = "group1")
    public void consume(String message) throws Exception {
        //log.debug("[PaymentConsumer Consumed Message]  {}", message);
        //message가 정상적으로 kafka 에 전송되었을 경우
        //예약처리 service 호출
        log.debug("[Logic Check][PaymentConsumer] 6-1. PaymentConsumer 받음");
        cashFacade.paymentWithKafka(message, false);
    }
}
