package com.hh.consertreservation.interfaces.consumer.point;

import com.hh.consertreservation.application.facade.CashFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointConsumer {

    private final CashFacade cashFacade;

    @KafkaListener(topics = "paymentinfo", groupId = "group2")
    public void consume(String message) {
        //log.debug("[PointConsumer Consumed Message]  {}", message);
        //message가 정상적으로 kafka 에 전송되었을 경우
        //아웃박스테이블에 init 으로 들어가있던 데이터를 -> published 로 처리
        log.debug("[Logic Check][PointConsumer] 6-2. PointConsumer 받음");
        cashFacade.publishPaymentoutBox(message);
    }
}
