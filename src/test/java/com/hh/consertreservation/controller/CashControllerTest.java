package com.hh.consertreservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.consertreservation.controller.dto.Charge;
import com.hh.consertreservation.controller.dto.Payment;
import com.hh.consertreservation.domain.dto.ReservationInfo;
import com.hh.consertreservation.domain.dto.User;
import com.hh.consertreservation.domain.dto.UserBalance;
import com.hh.consertreservation.domain.dto.servicerequest.PaymentServiceRequestDto;
import com.hh.consertreservation.domain.service.CashService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CashController.class)
class CashControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objMapper;

    @MockBean
    CashService cashService;


    /**
     * 잔액조회 , 대기열x
     */
    @Test
    void balance() throws Exception {
        UserBalance userBalance = UserBalance.builder().build();
        userBalance.setMockData();

        given(cashService.getUserBalance(1L)).willReturn(Optional.of(userBalance));

        mockMvc.perform(get("/cash/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.balance").exists())
                .andExpect(jsonPath("$.data.balance.userId").exists())
                .andExpect(jsonPath("$.data.balance.balance").exists())
                .andExpect(jsonPath("$.data.balance.updatedAt").exists());
    }


    /**
     * 잔액 충전 API
     * 대기열X
     */
    @Test
    void charge() throws Exception {
        long chargeAmount = 10000L;
        UserBalance userBalance = UserBalance.builder().build();
        userBalance.setMockData();

        Charge.Request req = new Charge.Request(userBalance.getUserId(), chargeAmount);

        given(cashService.charge(req.getUserId(), req.getAmount()))
                .willReturn(Optional.of(userBalance));
        mockMvc.perform(patch("/cash/charge")
                        .content(objMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.balance").exists())
                .andExpect(jsonPath("$.data.balance.userId").exists())
                .andExpect(jsonPath("$.data.balance.balance").exists())
                .andExpect(jsonPath("$.data.balance.updatedAt").exists());
    }

    /**
     * 결제API
     */
    @Test
    void payment() throws Exception {
        User user = User.builder().build();
        user.setMockData();
        ReservationInfo reservationInfo = ReservationInfo.builder().build();
        reservationInfo.setMockData();

        /*Payment.Request req = Payment.Request.builder()
                .userId(user.getId())
                .concertId(reservationInfo.getConcert().getConcertId())
                .seatNo(reservationInfo.getSeat().getSeatNumber())
                .concertDateTime(reservationInfo.getConcert().getSchedules().getConcertDate())
                .build();

        given(cashService.payment(any(PaymentServiceRequestDto.class)))
                .willReturn(Optional.of(reservationInfo));

        mockMvc.perform(post("/cash/payment")
                        .content(objMapper.writeValueAsString(req))
                        .header("Queue-Token", "tokenTest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.user").exists())
                .andExpect(jsonPath("$.data.reservation").exists())
                .andExpect(jsonPath("$.data.reservation.seat").exists())
                .andExpect(jsonPath("$.data.user.balance").exists());*/
    }
}