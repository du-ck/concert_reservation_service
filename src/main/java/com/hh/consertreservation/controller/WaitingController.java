package com.hh.consertreservation.controller;

import com.hh.consertreservation.application.facade.TokenFacade;
import com.hh.consertreservation.application.scheduler.WaitingScheduler;
import com.hh.consertreservation.controller.dto.TokenIssued;
import com.hh.consertreservation.controller.dto.data.ResponseData;
import com.hh.consertreservation.domain.dto.Token;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class WaitingController {

    private final TokenFacade tokenFacade;

    @PostMapping("/issued")
    @Operation(summary = "대기열 토큰 발급", description = "대기열 토큰을 발급한다.")
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

    /**
     * 대기열 순번을 알 수 있는 api
     * @return
     */
    @GetMapping("/waiting")
    @Operation(summary = "대기열 순번 조회", description = "다음 OnGoing 우선순위의 토큰id를 조회한다")
    public ResponseEntity<ResponseData> waitingCount() {
        long nextOnGoingUserId = WaitingScheduler.nextOnGoingTokenId;

        return new ResponseEntity<>(ResponseData.builder()
                .isSuccess(true)
                .code("200")
                .data(nextOnGoingUserId)
                .build(), HttpStatus.OK);
    }
}
