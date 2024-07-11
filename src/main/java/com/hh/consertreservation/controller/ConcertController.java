package com.hh.consertreservation.controller;

import com.hh.consertreservation.application.facade.ConcertFacade;
import com.hh.consertreservation.application.facade.TokenFacade;
import com.hh.consertreservation.controller.dto.ConcertDates;
import com.hh.consertreservation.controller.dto.ConcertSeats;
import com.hh.consertreservation.controller.dto.Reservation;
import com.hh.consertreservation.controller.dto.data.ResponseData;
import com.hh.consertreservation.domain.dto.Concert;
import com.hh.consertreservation.domain.dto.ConcertSchedule;
import com.hh.consertreservation.domain.dto.Seat;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class ConcertController {

    private final ConcertFacade concertFacade;
    private final TokenFacade tokenFacade;

    /**
     * 콘서트 예약가능 날짜 조회 API
     * 토큰검증 X
     * @return
     */
    @GetMapping("/dates")
    @Operation(summary = "콘서트 예약가능 날짜 조회", description = "해당 콘서트의 예약가능한 날짜를 조회한다.")
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
    @Operation(summary = "예약가능 좌석 조회", description = "해당 날짜에 예약가능한 좌석을 조회한다.")
    public ResponseEntity<ResponseData> concertSeats(
            ConcertSeats.Request req,
            @RequestHeader("Queue-Token") String queueToken) throws Exception {

        //토큰 검증
        tokenFacade.verification(req.getUserId(), queueToken);

        List<Seat> seats = concertFacade.getSeats(req.getConcertId(), req.getConcertDateTime());

        ConcertSeats.Response response = ConcertSeats.Response.builder()
                .seats(seats)
                .build();

        return new ResponseEntity<>(ResponseData.builder()
                .isSuccess(true)
                .code("200")
                .data(response)
                .build(), HttpStatus.OK);
    }

    /**
     * 좌석 예약 요청 API
     * @return
     */
    @PostMapping("/reservation")
    @Operation(summary = "좌석 예약", description = "선택한 좌석을 임시배정 처리한다. 5분안에 결제가 없을 시 임시배정이 풀린다.")
    public ResponseEntity<ResponseData> reservation(
            @RequestBody Reservation.Request req,
            @RequestHeader("Queue-Token") String queueToken) throws Exception {

        //토큰 검증
        tokenFacade.verification(req.getUserId(), queueToken);
        Optional<Seat> seat = concertFacade.reservation(req.getScheduleId(), req.getSeatNumber());

        Reservation.Response response = Reservation.Response.builder()
                .seat(seat.get())
                .build();

        return new ResponseEntity<>(ResponseData.builder()
                .isSuccess(true)
                .code("200")
                .data(response)
                .build(), HttpStatus.OK);
    }
}
