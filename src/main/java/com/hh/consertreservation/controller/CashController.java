package com.hh.consertreservation.controller;

import com.hh.consertreservation.application.facade.CashFacade;
import com.hh.consertreservation.application.facade.TokenFacade;
import com.hh.consertreservation.controller.dto.Balance;
import com.hh.consertreservation.controller.dto.Charge;
import com.hh.consertreservation.controller.dto.Payment;
import com.hh.consertreservation.controller.dto.data.ResponseData;
import com.hh.consertreservation.domain.dto.ReservationInfo;
import com.hh.consertreservation.domain.dto.User;
import com.hh.consertreservation.domain.dto.UserBalance;
import com.hh.consertreservation.domain.service.CashService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cash")
public class CashController {

    private final CashFacade cashFacade;
    private final TokenFacade tokenFacade;

    /**
     * 잔액 조회 API
     * 대기열X
     * @return
     */
    @GetMapping("/balance")
    public ResponseEntity<ResponseData> balance(Balance.Request req) throws Exception {
        Optional<UserBalance> userBalance = cashFacade.getUserBalance(req.getUserId());

        Balance.Response response = Balance.Response.builder()
                .balance(userBalance.get())
                .build();

        return new ResponseEntity<>(ResponseData.builder()
                .isSuccess(true)
                .code("200")
                .data(response)
                .build(), HttpStatus.OK);
    }

    /**
     * 잔액 충전 API
     * 대기열X
     * @return
     */
    @PatchMapping("/charge")
    public ResponseEntity<ResponseData> charge(@RequestBody Charge.Request req) throws Exception {
        Optional<UserBalance> balance = cashFacade.charge(req.getUserId(), req.getAmount());

        Balance.Response response = Balance.Response.builder()
                .balance(balance.get())
                .build();
        return new ResponseEntity<>(ResponseData.builder()
                .isSuccess(true)
                .code("200")
                .data(response)
                .build(), HttpStatus.OK);
    }

    /**
     * 결제API
     * @return
     */
    @PostMapping("/payment")
    public ResponseEntity<ResponseData> payment(
            @RequestBody Payment.Request req,
            @RequestHeader("Queue-Token") String queueToken) throws Exception {

        //토큰 검증
        tokenFacade.verification(req.getUserId(), queueToken);

        Optional<ReservationInfo> reservation = cashFacade.payment(Payment.toServiceRequestDto(req, queueToken));

        Payment.Response response = Payment.Response.builder()
                .reservation(reservation.get())
                .build();

        return new ResponseEntity<>(ResponseData.builder()
                .isSuccess(true)
                .code("200")
                .data(response)
                .build(), HttpStatus.OK);
    }
}
