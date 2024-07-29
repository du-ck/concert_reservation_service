package com.hh.consertreservation.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.consertreservation.application.facade.ConcertFacade;
import com.hh.consertreservation.application.facade.TokenFacade;
import com.hh.consertreservation.interfaces.concert.ConcertController;
import com.hh.consertreservation.interfaces.concert.ConcertDates;
import com.hh.consertreservation.interfaces.concert.ConcertSeats;
import com.hh.consertreservation.interfaces.concert.Reservation;
import com.hh.consertreservation.domain.concert.Concert;
import com.hh.consertreservation.domain.concert.ConcertSchedule;
import com.hh.consertreservation.domain.concert.Seat;
import com.hh.consertreservation.domain.concert.ConcertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objMapper;

    @MockBean
    private ConcertService concertService;

    @MockBean
    private ConcertFacade concertFacade;

    @MockBean
    private TokenFacade tokenFacade;

    @Test
    void concertDates() throws Exception {

        ConcertDates.Request req = new ConcertDates.Request();
        req.setConcertId(100L);
        List<ConcertSchedule> schedules = Concert.getMockListData();

        given(concertFacade.getDates(req.getConcertId()))
                .willReturn(schedules);

        mockMvc.perform(get("/api/concert/dates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Queue-Token", "tokenTest")
                        .param("concertId", String.valueOf(req.getConcertId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.concerts").exists())
                .andExpect(jsonPath("$.data.concerts.concertId").exists())
                .andExpect(jsonPath("$.data.concerts.schedules[*].title").exists())
                .andExpect(jsonPath("$.data.concerts.schedules[*].concertDate").exists())
                .andExpect(jsonPath("$.data.concerts.schedules[*].description").exists())
                .andExpect(jsonPath("$.data.concerts.schedules[*].price").exists())
                .andExpect(jsonPath("$.data.concerts.schedules[*].seats").exists());
    }

    @Test
    void concertSeats() throws Exception {
        ConcertSeats.Request req = new ConcertSeats.Request();
        req.setUserId(1L);
        req.setConcertId(1L);
        req.setConcertDateTime("2024-07-10 11:30");
        List<Seat> seats = Seat.getMockListData();
        given(concertFacade.getSeats(req.getConcertId(), req.getConcertDateTime()))
                .willReturn(seats);

        mockMvc.perform(get("/api/concert/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(req.getUserId()))
                        .param("concertId", String.valueOf(req.getConcertId()))
                        .param("concertDateTime", String.valueOf(req.getConcertDateTime()))
                        .header("Queue-Token", "tokenTest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.seats").exists())
                .andExpect(jsonPath("$.data.seats[*].id").exists())
                .andExpect(jsonPath("$.data.seats[*].seatNumber").exists())
                .andExpect(jsonPath("$.data.seats[*].status").exists())
                .andExpect(jsonPath("$.data.seats[*].scheduleId").exists());
    }

    @Test
    void reservation() throws Exception {
        /*private Long scheduleId;
        private Long userId;
        private Long seatNo;*/
        Reservation.Request req = new Reservation.Request();
        req.setUserId(1L);
        req.setScheduleId(1L);
        req.setSeatNumber(22L);
        Seat seat = Seat.builder().build();
        seat.setMockData();

        given(concertFacade.reserve(req.getScheduleId(), req.getSeatNumber()))
                .willReturn(Optional.of(seat));

        mockMvc.perform(post("/api/concert/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(req))
                        .header("Queue-Token", "tokenTest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.seat").exists())
                .andExpect(jsonPath("$.data.seat.id").exists())
                .andExpect(jsonPath("$.data.seat.seatNumber").exists())
                .andExpect(jsonPath("$.data.seat.status").exists());
    }
}