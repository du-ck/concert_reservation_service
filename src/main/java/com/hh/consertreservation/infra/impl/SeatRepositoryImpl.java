package com.hh.consertreservation.infra.impl;

import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.dto.types.SeatType;
import com.hh.consertreservation.domain.repository.SeatRepository;
import com.hh.consertreservation.infra.entity.SeatEntity;
import com.hh.consertreservation.infra.jpa.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Override
    public Optional<Seat> getSeatForReservation(long scheduleId, long seatNumber) {
        Optional<SeatEntity> entity = jpaRepository.findSeatForReservation(scheduleId, seatNumber);
        if (entity.isPresent()) {
            return Optional.of(SeatEntity.toDomain(entity.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Seat> save(Seat seat) {
        Optional<SeatEntity> entity = Optional.of(jpaRepository.save(SeatEntity.toEntity(seat)));
        if (entity.isPresent()) {
            return Optional.of(SeatEntity.toDomain(entity.get()));
        }
        return Optional.empty();
    }

    @Override
    public void setSeatStatusEmpty() {
        jpaRepository.setEmptyForTempSeat(LocalDateTime.now().minusMinutes(5));
    }
}
