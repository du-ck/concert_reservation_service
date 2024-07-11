package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.repository.ConcertRepository;
import com.hh.consertreservation.domain.repository.ScheduleRepository;
import com.hh.consertreservation.domain.repository.SeatRepository;
import com.hh.consertreservation.infra.entity.ScheduleEntity;
import com.hh.consertreservation.infra.entity.SeatEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @InjectMocks
    ConcertService concertService;

    @Mock
    SeatRepository seatRepository;

    @Mock
    ScheduleRepository scheduleRepository;

    private Long userId;
    private Long concertId;
    private String concertDateTime;

    @BeforeEach
    void setUp() {
        userId = 1L;
        concertId = 10L;
        concertDateTime = "2024-07-12 13:00";
    }

    @Test
    void 날짜조회() {
        List<ConcertSchedule> concertSchedules = Concert.getMockListData();
        given(scheduleRepository.findAllDates(anyLong()))
                .willReturn(concertSchedules);

        List<ConcertSchedule> resultList = concertService.getDates(1L);

        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(concertSchedules.size(), resultList.size());
    }

    @Test
    void 좌석조회() {
        ConcertSchedule schedule = ConcertSchedule.builder()
                .scheduleId(1L)
                .concertId(concertId)
                .title("짱구는 못말려 극장판 9기 어른제국의 역습")
                .concertDate(concertDateTime)
                .description("개꿀잼")
                .price(15000L)
                .seats(50L)
                .build();
        List<Seat> seats = Seat.getMockListData();

        given(scheduleRepository.getScheduleId(concertId, concertDateTime))
                .willReturn(Optional.of(schedule));
        given(seatRepository.getSeats(schedule.getScheduleId()))
                .willReturn(seats);

        List<Seat> resultList = concertService.getSeats(concertId, concertDateTime);

        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(seats.size(), resultList.size());
    }
}