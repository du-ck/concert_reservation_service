package com.hh.consertreservation.infra.concert;

import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.ScheduleRepository;
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
    public Optional<ConcertSchedule> getScheduleIdWithLock(long concertId, String concertDateTime) {
        Optional<ScheduleEntity> entity = jpaRepository.findScheduleIdWithLock(concertId, concertDateTime);
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
