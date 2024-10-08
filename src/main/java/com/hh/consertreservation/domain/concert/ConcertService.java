package com.hh.consertreservation.domain.concert;

import com.hh.consertreservation.support.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcertService {

    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;

    private final ConcertRepository concertRepository;

    @Cacheable(value = "concerts", cacheManager = "cacheManager")
    public List<ConcertTitle> getConcerts() {
        return concertRepository.findAll();
    }

    public List<ConcertTitle> getConcertsNoCacheForTest() {
        return concertRepository.findAll();
    }

    @Cacheable(value = "concertSchedules", key = "#concertId", cacheManager = "cacheManager")
    public List<ConcertSchedule> getDates(long concertId) {
        return scheduleRepository.findAllDates(concertId);
    }
    public List<ConcertSchedule> getDatesNoCacheForTest(long concertId) {
        return scheduleRepository.findAllDates(concertId);
    }

    public List<Seat> getSeats(long concertId, String concertDateTime) {
        //concertId 와 concertDateTime 으로 스케쥴id를 찾는다.
        Optional<ConcertSchedule> schedule = scheduleRepository.getScheduleIdWithLock(concertId, concertDateTime);
        if (schedule.isPresent()) {
            return seatRepository.getSeats(schedule.get().getScheduleId());
        }
        return new ArrayList<>();
    }


    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Optional<Seat> reserve(long scheduleId, long seatNumber) throws Exception {

        Optional<Seat> seatForReservation = seatRepository.getSeatForReserve(scheduleId, seatNumber);   //lock 획득
        if (!seatForReservation.isPresent()) {
            throw new ResourceNotFoundException("해당 좌석 없음");
        }
        seatForReservation.get().reservation(); //예약처리 (좌석임시, update날짜 갱신)

        Optional<Seat> saveSeat = seatRepository.save(seatForReservation.get());

        if (!saveSeat.isPresent()) {
            throw new ResourceNotFoundException("좌석 예약 실패");
        }
        return saveSeat;
    }

    /**
     * 임시배정 해제 (스케쥴러 용)
     */
    @Transactional
    public void setEmptySeat() {
        seatRepository.setSeatStatusEmpty();
    }

    public Optional<Seat> getSeatWithId(long seatId) {
        Optional<Seat> seat = seatRepository.findByIdWithTemp(seatId);
        if (!seat.isPresent()) {
            throw new IllegalArgumentException("좌석이 없습니다");
        }
        return seat;
    }

    public Optional<ConcertSchedule> getScheduleWithId(long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    public Optional<Seat> setReservedSeat(long seatId) {
        Optional<Seat> reservedSeat = seatRepository.setSeatReserved(seatId);
        if (!reservedSeat.isPresent()) {
            throw new IllegalStateException("좌석 배정처리 실패");
        }
        return reservedSeat;
    }
}
