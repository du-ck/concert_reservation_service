package com.hh.consertreservation.controller;

import com.hh.consertreservation.application.facade.ConcertFacade;
import com.hh.consertreservation.controller.dto.ConcertDates;
import com.hh.consertreservation.controller.dto.ConcertSeats;
import com.hh.consertreservation.controller.dto.Reservation;
import com.hh.consertreservation.controller.dto.data.ResponseData;
import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class ConcertController {

    private final ConcertFacade concertFacade;

    /**
     * 콘서트 예약가능 날짜 조회 API
     * 토큰검증 X
     * @return
     */
    @GetMapping("/dates")
    public ResponseEntity<ResponseData> concertDates(
            ConcertDates.Request req) {
        List<ConcertSchedule> schedules = concertFacade.getDates(req.getConcertId());

        ConcertDates.Response response = ConcertDates.Response.builder()
                .concerts(Concert.builder()
                        .concertId(req.getConcertId())
                        .schedules(schedules)
                        .build())
                .build();

        return new ResponseEntity<>(ResponseData.builder()
                .isSuccess(true)
                .code("200")
                .data(response)
                .build(), HttpStatus.OK);
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
