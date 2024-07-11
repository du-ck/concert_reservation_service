package com.hh.consertreservation.controller;

import com.hh.consertreservation.application.facade.TokenFacade;
import com.hh.consertreservation.application.scheduler.WaitingScheduler;
import com.hh.consertreservation.controller.dto.TokenIssued;
import com.hh.consertreservation.controller.dto.data.ResponseData;
import com.hh.consertreservation.domain.dto.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class WaitingController {

    private final TokenFacade tokenFacade;

    @PostMapping("/issued")
    public ResponseEntity<ResponseData> tokenIssued(@RequestBody TokenIssued.Request req) throws Exception {

        Optional<Token> token = tokenFacade.issued(req.getUserId(), WaitingScheduler.MAXIMUM_ONGOING_COUNT);

        TokenIssued.Response response = TokenIssued.Response.builder()
                .userId(token.get().getUserId())
                .queueToken(token.get().getQueueToken())
                .build();

        return new ResponseEntity<>(ResponseData.builder()
                .isSuccess(true)
                .code("200")
                .data(response)
                .build(), HttpStatus.OK);
    }
}
