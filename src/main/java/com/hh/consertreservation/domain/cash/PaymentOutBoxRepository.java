package com.hh.consertreservation.domain.cash;

import java.util.List;
import java.util.Optional;

public interface PaymentOutBoxRepository {
    Optional<PaymentOutBox> save(PaymentOutBox paymentOutBox);

    Optional<PaymentOutBox> findByMessage(String message);

    List<PaymentOutBox> findAllByInit();
}
