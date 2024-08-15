package com.hh.consertreservation.infra.cash;

import com.hh.consertreservation.domain.cash.PaymentOutBox;
import com.hh.consertreservation.domain.cash.PaymentOutBoxRepository;
import com.hh.consertreservation.domain.cash.PaymentOutBoxType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentOutBoxRepositoryImpl implements PaymentOutBoxRepository {

    private final PaymentOutBoxJpaRepository jpaRepository;

    @Override
    public Optional<PaymentOutBox> save(PaymentOutBox paymentOutBox) {
        Optional<PaymentOutBoxEntity> entity = Optional.of(jpaRepository.save(PaymentOutBoxEntity.toEntity(paymentOutBox)));

        if (entity.isPresent()) {
            return Optional.of(PaymentOutBoxEntity.toDomain(entity.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<PaymentOutBox> findByMessage(String message) {
        Optional<PaymentOutBoxEntity> entity = Optional.of(jpaRepository.findByMessage(message));
        if (entity.isPresent()) {
            return Optional.of(PaymentOutBoxEntity.toDomain(entity.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<PaymentOutBox> findAllByInit() {
        return PaymentOutBoxEntity.toDomainList(jpaRepository.findAllByStatus(PaymentOutBoxType.INIT));
    }
}
