package com.hh.consertreservation.infra.cash;

import com.hh.consertreservation.domain.cash.ReservationInfo;
import com.hh.consertreservation.domain.cash.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public List<ReservationInfo> findByUserIdAndScheduleId(long userId, long scheduleId) {
        List<ReservationEntity> entities = jpaRepository.findByUserIdAndScheduleId(userId, scheduleId);
        if (!CollectionUtils.isEmpty(entities)) {
            return ReservationEntity.toDomainList(entities);
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
