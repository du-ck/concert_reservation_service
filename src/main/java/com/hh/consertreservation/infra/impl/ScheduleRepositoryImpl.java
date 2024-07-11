package com.hh.consertreservation.infra.impl;

import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;
import com.hh.consertreservation.domain.repository.ScheduleRepository;
import com.hh.consertreservation.infra.entity.ScheduleEntity;
import com.hh.consertreservation.infra.jpa.ScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleJpaRepository jpaRepository;

    @Override
    public List<ConcertSchedule> findAllDates(long concertId) {
        List<ScheduleEntity> scheduleEntities = jpaRepository.findAllByConcertId(concertId);
        return ScheduleEntity.toDomainList(scheduleEntities);
    }
}
