package com.hh.consertreservation.controller;

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

    private final CashService cashService;

    /**
     * 잔액 조회 API
     * 대기열X
     * @return
     */
    @GetMapping("/balance")
    public ResponseEntity<ResponseData> balance(Balance.Request req) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * 잔액 충전 API
     * 대기열X
     * @return
     */
    @PatchMapping("/charge")
    public ResponseEntity<ResponseData> charge(@RequestBody Charge.Request req) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * 결제API
     * @return
     */
    @PostMapping("/payment")
    public ResponseEntity<ResponseData> payment(
            @RequestBody Payment.Request req,
            @RequestHeader("Queue-Token") String queueToken) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
