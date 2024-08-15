package com.hh.consertreservation.interfaces.api.waiting;

import com.hh.consertreservation.application.facade.TokenFacade;
import com.hh.consertreservation.interfaces.api.dto.ResponseData;
import com.hh.consertreservation.interfaces.api.scheduler.WaitingScheduler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class WaitingController {

    private final TokenFacade tokenFacade;

    @PostMapping("/issued")
    @Operation(summary = "대기열 토큰 발급", description = "대기열 토큰을 발급한다.")
    public ResponseEntity<ResponseData> tokenIssued(@RequestBody TokenIssued.Request req) throws Exception {

        String token = tokenFacade.issue(req.getUserId(), WaitingScheduler.MAXIMUM_ONGOING_COUNT);

        TokenIssued.Response response = TokenIssued.Response.builder()
                .queueToken(token)
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
    @Operation(summary = "대기열 순번 조회", description = "대기열 몇번째 순서인지 조회한다")
    public ResponseEntity<ResponseData> waitingCount(@RequestHeader("Queue-Token") String queueToken) {
        Optional<Long> position = tokenFacade.getWaitingPosition(queueToken);

        return new ResponseEntity<>(ResponseData.builder()
                .isSuccess(true)
                .code("200")
                .data(position)
                .build(), HttpStatus.OK);
    }
}
