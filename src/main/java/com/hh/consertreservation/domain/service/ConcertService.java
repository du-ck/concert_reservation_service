package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.repository.ScheduleRepository;
import com.hh.consertreservation.domain.repository.SeatRepository;
import com.hh.consertreservation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;

    public List<ConcertSchedule> getDates(long concertId) {
        return scheduleRepository.findAllDates(concertId);
    }

    public List<Seat> getSeats(long concertId, String concertDateTime) {
        //concertId 와 concertDateTime 으로 스케쥴id를 찾는다.
        Optional<ConcertSchedule> schedule = scheduleRepository.getScheduleId(concertId, concertDateTime);
        if (schedule.isPresent()) {
            return seatRepository.getSeats(schedule.get().getScheduleId());
        }
        return new ArrayList<>();
    }


    public Optional<Seat> reservation(long scheduleId, long seatNumber) throws Exception {
        Optional<Seat> seatForReservation = seatRepository.getSeatForReservation(scheduleId, seatNumber);
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
        return seatRepository.findById(seatId);
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
