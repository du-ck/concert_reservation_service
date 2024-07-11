package com.hh.consertreservation.infra.impl;

import com.hh.consertreservation.domain.dto.ReservationInfo;
import com.hh.consertreservation.domain.repository.ReservationRepository;
import com.hh.consertreservation.infra.entity.ReservationEntity;
import com.hh.consertreservation.infra.jpa.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;

    @Override
    public Optional<ReservationInfo> save(ReservationInfo reservationInfo) {

        Optional<ReservationEntity> saveEntity = Optional.of(jpaRepository.save(ReservationEntity.toEntity(reservationInfo)));

        if (saveEntity.isPresent()) {
            return Optional.of(ReservationEntity.toDomain(saveEntity.get()));
        }
        return Optional.empty();
    }
}
