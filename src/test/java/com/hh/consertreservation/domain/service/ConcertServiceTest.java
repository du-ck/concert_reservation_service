package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.concert.*;
import com.hh.consertreservation.domain.concert.SeatType;
import com.hh.consertreservation.support.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @InjectMocks
    ConcertService concertService;

    @Mock
    SeatRepository seatRepository;

    @Mock
    ScheduleRepository scheduleRepository;

    @Mock
    ConcertRepository concertRepository;

    private Long userId;
    private Long concertId;
    private Long scheduleId;
    private String concertDateTime;

    @BeforeEach
    void setUp() {
        userId = 1L;
        scheduleId = 1L;
        concertId = 10L;
        concertDateTime = "2024-07-12 13:00";
    }

    @Test
    void 목록조회() {
        List<ConcertTitle> concerts = new ArrayList<>();
        concerts.add(ConcertTitle.builder()
                .concertId(100L)
                .title("짱구는 못말려 극장판 8기")
                .build());
        concerts.add(ConcertTitle.builder()
                .concertId(200L)
                .title("짱구는 못말려 극장판 9기")
                .build());
        concerts.add(ConcertTitle.builder()
                .concertId(300L)
                .title("짱구는 못말려 극장판 10기")
                .build());
        given(concertRepository.findAll())
                .willReturn(concerts);

        List<ConcertTitle> resultList = concertService.getConcerts();
        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(concerts.size(), resultList.size());
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

        given(scheduleRepository.getScheduleIdWithLock(concertId, concertDateTime))
                .willReturn(Optional.of(schedule));
        given(seatRepository.getSeats(schedule.getScheduleId()))
                .willReturn(seats);

        List<Seat> resultList = concertService.getSeats(concertId, concertDateTime);

        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(seats.size(), resultList.size());
    }

    @Test
    void 좌석예약테스트() throws Exception {
        Seat seat = Seat.builder().build();
        seat.setMockData();
        given(seatRepository.getSeatForReserve(scheduleId, 1L))
                .willReturn(Optional.of(seat));

        seat.reservation();
        given(seatRepository.save(seat))
                .willReturn(Optional.of(seat));

        Optional<Seat> resultSeat = concertService.reserve(scheduleId, 1L);

        Assertions.assertEquals(resultSeat.get().getStatus(), SeatType.TEMPORARILY);
        Assertions.assertEquals(resultSeat.get().getSeatNumber(), 1L);
    }

    @Test
    void 좌석예약_좌석없음() {
        Seat seat = Seat.builder().build();
        seat.setMockData();
        given(seatRepository.getSeatForReserve(scheduleId, 1L))
                .willReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> concertService.reserve(scheduleId, 1L));

        Assertions.assertEquals("해당 좌석 없음", exception.getMessage());
    }

    @Test
    void 좌석예약_실패() throws Exception {
        Seat seat = Seat.builder().build();
        seat.setMockData();
        given(seatRepository.getSeatForReserve(scheduleId, 1L))
                .willReturn(Optional.of(seat));

        seat.reservation();
        given(seatRepository.save(seat))
                .willReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> concertService.reserve(scheduleId, 1L));

        Assertions.assertEquals("좌석 예약 실패", exception.getMessage());
    }
}