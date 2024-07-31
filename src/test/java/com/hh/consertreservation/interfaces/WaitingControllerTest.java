package com.hh.consertreservation.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.consertreservation.application.facade.TokenFacade;
import com.hh.consertreservation.interfaces.waiting.TokenIssued;
import com.hh.consertreservation.domain.waiting.Token;
import com.hh.consertreservation.domain.waiting.WaitingService;
import com.hh.consertreservation.interfaces.waiting.WaitingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WaitingController.class)
class WaitingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objMapper;

    @MockBean
    private WaitingService waitingService;

    @MockBean
    private TokenFacade tokenFacade;

    @Test
    void tokenIssued() throws Exception {
        TokenIssued.Request req = TokenIssued.Request.builder()
                .userId(1L)
                .build();
        Token token = Token.builder().build();
        token.setMockData();
        given(tokenFacade.issued(req.getUserId(), 500))
                .willReturn("test:1");

        mockMvc.perform(post("/api/token/issued")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.queueToken").exists());
    }
}