package com.hh.consertreservation.controller;

import com.hh.consertreservation.controller.dto.TokenIssued;
import com.hh.consertreservation.controller.dto.data.ResponseData;
import com.hh.consertreservation.domain.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping("/issued")
    public ResponseEntity<ResponseData> tokenIssued(@RequestBody TokenIssued.Request req) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
