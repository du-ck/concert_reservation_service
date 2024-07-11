package com.hh.consertreservation.infra.impl;

import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.repository.SeatRepository;
import com.hh.consertreservation.infra.entity.SeatEntity;
import com.hh.consertreservation.infra.jpa.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {

    private final SeatJpaRepository jpaRepository;

    @Override
    public List<Seat> getSeats(long scheduleId) {
        List<SeatEntity> seatEntities = jpaRepository.findEmptySeat(scheduleId);
        return SeatEntity.toDomainList(seatEntities);
    }
}
