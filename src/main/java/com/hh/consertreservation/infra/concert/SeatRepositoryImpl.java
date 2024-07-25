package com.hh.consertreservation.infra.concert;

import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.concert.SeatType;
import com.hh.consertreservation.domain.concert.SeatRepository;
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
        Optional<SeatEntity> entity = jpaRepository.findSeatForReservationWithLock(scheduleId, seatNumber);
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

    @Override
    public Optional<Seat> findByIdWithTemp(long seatId) {
        Optional<SeatEntity> seatEntity = jpaRepository.findByIdAndStatus(seatId, SeatType.TEMPORARILY);
        if (seatEntity.isPresent()) {
            return Optional.of(SeatEntity.toDomain(seatEntity.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Seat> setSeatReserved(long seatId) {
        Optional<SeatEntity> seatEntity = jpaRepository.findById(seatId);

        if (seatEntity.isPresent()) {

            SeatEntity saveEnitity = seatEntity.get();
            saveEnitity = saveEnitity.toBuilder()
                    .status(SeatType.RESERVED)
                    .updatedAt(LocalDateTime.now())
                    .build();

            Optional<SeatEntity> result = Optional.of(jpaRepository.save(saveEnitity));
            if (result.isPresent()) {
                return Optional.of(SeatEntity.toDomain(result.get()));
            }
        }
        return Optional.empty();
    }

    @Override
    public void setSeatAllEmptyForTest(long scheduleId) {
        List<SeatEntity> seatEntities = jpaRepository.findByScheduleId(scheduleId);
        seatEntities = seatEntities.stream().map(
                m -> m.toBuilder().status(SeatType.EMPTY)
                        .build()
        ).toList();
        jpaRepository.saveAll(seatEntities);
    }
}
