package com.hh.consertreservation.support.platform;

import com.hh.consertreservation.domain.cash.ReservationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentMockApiClient {

    public void sendPaymentInfo(ReservationInfo reservationInfo) {
        log.debug("SEND!!!!");
        log.debug(reservationInfo.toString());
    }
}
