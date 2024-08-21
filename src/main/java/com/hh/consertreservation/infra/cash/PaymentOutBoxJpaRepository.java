package com.hh.consertreservation.infra.cash;

import com.hh.consertreservation.domain.cash.PaymentOutBoxType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentOutBoxJpaRepository extends JpaRepository<PaymentOutBoxEntity, Long> {
    List<PaymentOutBoxEntity> findAllByStatus(PaymentOutBoxType status);

    PaymentOutBoxEntity findByMessage(String message);
}
