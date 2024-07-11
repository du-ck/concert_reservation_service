package com.hh.consertreservation.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class Concert {
    private Long concertId;
    List<ConcertSchedule> schedules;

    public void setMockData() {
        this.concertId = 100L;
        this.schedules = getMockListData();
    }

    public static List<ConcertSchedule> getMockListData() {

        List<ConcertSchedule> concertSchedules = new ArrayList<>();
        concertSchedules.add(ConcertSchedule.builder()
                .concertId(100L)
                .title("짱구는 못말려 극장판 9기 어른제국의 역습")
                .concertDate("2024-07-04 22:30")
                .description("개꿀잼")
                .price(15000L)
                .seats(50L)
                .build());
        concertSchedules.add(ConcertSchedule.builder()
                .concertId(100L)
                .title("짱구는 못말려 극장판 9기 어른제국의 역습")
                .concertDate("2024-07-05 19:30")
                .description("개꿀잼")
                .price(15000L)
                .seats(50L)
                .build());
        concertSchedules.add(ConcertSchedule.builder()
                .concertId(100L)
                .title("짱구는 못말려 극장판 9기 어른제국의 역습")
                .concertDate("2024-07-06 20:30")
                .description("개꿀잼")
                .price(15000L)
                .seats(50L)
                .build());
        return concertSchedules;
    }
}
