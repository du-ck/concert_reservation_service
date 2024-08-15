package com.hh.consertreservation.interfaces.api.scheduler;

import com.hh.consertreservation.application.facade.CashFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentOutBoxScheduler {
    private final CashFacade cashFacade;

    @Scheduled(fixedRate = 10000)
    public void resendKafkaMessage() {
        cashFacade.resendKafka();
    }
}
