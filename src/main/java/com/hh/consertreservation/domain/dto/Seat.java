package com.hh.consertreservation.domain.dto;

import com.hh.consertreservation.domain.dto.types.SeatType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class Seat {
    private Long id;
    private Long concertId;
    private Long seatNumber;
    private SeatType status;
    private LocalDateTime updatedAt;

    public void setMockData() {
        this.id = 1L;
        this.concertId = 100L;
        this.seatNumber = 1L;
        this.status = SeatType.RESERVED;
        this.updatedAt = LocalDateTime.now();
    }

    public static List<Seat> getMockListData() {

        List<Seat> seats = new ArrayList<>();
        seats.add(Seat.builder()
                .id(1L)
                .concertId(100L)
                .seatNumber(22L)
                .status(SeatType.EMPTY)
                .updatedAt(LocalDateTime.now())
                .build());
        seats.add(Seat.builder()
                .id(2L)
                .concertId(100L)
                .seatNumber(23L)
                .status(SeatType.EMPTY)
                .updatedAt(LocalDateTime.now())
                .build());
        seats.add(Seat.builder()
                .id(3L)
                .concertId(100L)
                .seatNumber(24L)
                .status(SeatType.EMPTY)
                .updatedAt(LocalDateTime.now())
                .build());
        seats.add(Seat.builder()
                .id(4L)
                .concertId(100L)
                .seatNumber(25L)
                .status(SeatType.EMPTY)
                .updatedAt(LocalDateTime.now())
                .build());
        return seats;
    }
}
