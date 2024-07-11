package com.hh.consertreservation.infra.impl;

import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;
import com.hh.consertreservation.domain.repository.ScheduleRepository;
import com.hh.consertreservation.infra.entity.ScheduleEntity;
import com.hh.consertreservation.infra.jpa.ScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleJpaRepository jpaRepository;

    @Override
    public List<ConcertSchedule> findAllDates(long concertId) {
        List<ScheduleEntity> scheduleEntities = jpaRepository.findAllByConcertId(concertId);
        return ScheduleEntity.toDomainList(scheduleEntities);
    }

    @Override
    public Optional<ConcertSchedule> getScheduleId(long concertId, String concertDateTime) {
        Optional<ScheduleEntity> entity = jpaRepository.findScheduleId(concertId, concertDateTime);
        if (entity.isPresent()) {
            return Optional.of(ScheduleEntity.toDomain(entity.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ConcertSchedule> findById(long scheduleId) {
        Optional<ScheduleEntity> scheduleEntity = jpaRepository.findById(scheduleId);
        if (scheduleEntity.isPresent()) {
            return Optional.of(ScheduleEntity.toDomain(scheduleEntity.get()));
        }
        return Optional.empty();
    }
}
