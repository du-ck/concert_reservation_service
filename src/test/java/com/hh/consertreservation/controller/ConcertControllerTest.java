package com.hh.consertreservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.consertreservation.controller.dto.ConcertDates;
import com.hh.consertreservation.controller.dto.ConcertSeats;
import com.hh.consertreservation.controller.dto.Reservation;
import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.dto.servicerequest.ReservationServiceRequestDto;
import com.hh.consertreservation.domain.service.ConcertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
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

    @Test
    void concertDates() throws Exception {

        ConcertDates.Request req = ConcertDates.Request.builder()
                        .concertId(100L).build();

        given(concertService.getDates(req.getConcertId()))
                .willReturn(Optional.of(Concert.getMockListData()));

        mockMvc.perform(get("/concert/dates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Queue-Token", "tokenTest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.concerts").exists())
                .andExpect(jsonPath("$.data.concerts[*].concertId").exists())
                .andExpect(jsonPath("$.data.concerts[*].title").exists())
                .andExpect(jsonPath("$.data.concerts[*].concertDate").exists())
                .andExpect(jsonPath("$.data.concerts[*].description").exists())
                .andExpect(jsonPath("$.data.concerts[*].price").exists())
                .andExpect(jsonPath("$.data.concerts[*].seats").exists());
    }

    @Test
    void concertSeats() throws Exception {
        ConcertSeats.Request req = ConcertSeats.Request.builder()
                .concertId(1L)
                .concertDateTime("2024-07-10 11:30")
                .build();

        given(concertService.getSeats(req.getConcertId(), req.getConcertDateTime()))
                .willReturn(Optional.of(Seat.getMockListData()));

        mockMvc.perform(get("/concert/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Queue-Token", "tokenTest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.seats").exists())
                .andExpect(jsonPath("$.data.seats[*].concertId").exists())
                .andExpect(jsonPath("$.data.seats[*].id").exists())
                .andExpect(jsonPath("$.data.seats[*].seatNumber").exists())
                .andExpect(jsonPath("$.data.seats[*].status").exists())
                .andExpect(jsonPath("$.data.seats[*].updatedAt").exists());
    }

    @Test
    void reservation() throws Exception {
        Reservation.Request req = Reservation.Request.builder()
                .concertId(100L)
                .concertDateTime("2024-07-10 11:30")
                .userId(1L)
                .seatNo(22L)
                .build();
        Seat seat = Seat.builder().build();
        seat.setMockData();

        given(concertService.reservation(any(ReservationServiceRequestDto.class)))
                .willReturn(Optional.of(seat));

        mockMvc.perform(post("/concert/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(req))
                        .header("Queue-Token", "tokenTest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.seat").exists())
                .andExpect(jsonPath("$.data.result").exists())
                .andExpect(jsonPath("$.data.seat.concertId").exists())
                .andExpect(jsonPath("$.data.seat.id").exists())
                .andExpect(jsonPath("$.data.seat.seatNumber").exists())
                .andExpect(jsonPath("$.data.seat.status").exists())
                .andExpect(jsonPath("$.data.seat.updatedAt").exists());
    }
}