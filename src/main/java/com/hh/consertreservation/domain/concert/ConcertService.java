package com.hh.consertreservation.domain.concert;

import com.hh.consertreservation.support.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

    public List<ConcertSchedule> getDates(long concertId) {
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


    @Transactional
    public Optional<Seat> reservation(long scheduleId, long seatNumber) throws Exception {
        log.info("[쓰레드ID : {}] 서비스 시작!!",Thread.currentThread().getId());

        Optional<Seat> seatForReservation = seatRepository.getSeatForReservation(scheduleId, seatNumber);   //lock 획득
        if (!seatForReservation.isPresent()) {
            log.error("[쓰레드ID : {}] 해당 좌석 없음!!",Thread.currentThread().getId());
            throw new ResourceNotFoundException("해당 좌석 없음");
        }

        log.info("[쓰레드ID : {}] 예약 락 획득!!",Thread.currentThread().getId());
        seatForReservation.get().reservation(); //예약처리 (좌석임시, update날짜 갱신)

        Optional<Seat> saveSeat = seatRepository.save(seatForReservation.get());
        log.info("[쓰레드ID : {}] 예약처리 완료!!",Thread.currentThread().getId());

        if (!saveSeat.isPresent()) {
            throw new ResourceNotFoundException("좌석 예약 실패");
        }
        log.info("[쓰레드ID : {}] 서비스 끝!!",Thread.currentThread().getId());
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
