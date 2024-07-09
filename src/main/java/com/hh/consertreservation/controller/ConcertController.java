package com.hh.consertreservation.controller;

import com.hh.consertreservation.controller.dto.ConcertDates;
import com.hh.consertreservation.controller.dto.ConcertSeats;
import com.hh.consertreservation.controller.dto.Reservation;
import com.hh.consertreservation.controller.dto.data.ResponseData;
import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.Seat;
import com.hh.consertreservation.domain.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class ConcertController {

    private final ConcertService concertService;

    /**
     * 콘서트 예약가능 날짜 조회 API
     * @return
     */
    @GetMapping("/dates")
    public ResponseEntity<ResponseData> concertDates(
            ConcertDates.Request req,
            @RequestHeader("Queue-Token") String queueToken) {

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * 콘서트 예약가능 좌석 조회 API
     * @return
     */
    @GetMapping("/seats")
    public ResponseEntity<ResponseData> concertSeats(
            ConcertSeats.Request req,
            @RequestHeader("Queue-Token") String queueToken) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * 좌석 예약 요청 API
     * @return
     */
    @PostMapping("/reservation")
    public ResponseEntity<ResponseData> reservation(
            @RequestBody Reservation.Request req,
            @RequestHeader("Queue-Token") String queueToken) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
