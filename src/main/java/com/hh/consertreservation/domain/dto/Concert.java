package com.hh.consertreservation.domain.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class Concert {
    private Long concertId;
    private String title;
    private String concertDate;
    private String description;
    private Long price;
    private Long seats;

    public void setMockData() {
        this.concertId = 100L;
        this.title = "짱구는 못말려 극장판 9기 어른제국의 역습";
        this.concertDate = "2024-07-04 22:30";
        this.description = "개꿀잼";
        this.price = 15000L;
        this.seats = 50L;
    }

    public static List<Concert> getMockListData() {

        List<Concert> concerts = new ArrayList<>();
        concerts.add(Concert.builder()
                .concertId(100L)
                .title("짱구는 못말려 극장판 9기 어른제국의 역습")
                .concertDate("2024-07-04 22:30")
                .description("개꿀잼")
                .price(15000L)
                .seats(50L)
                .build());
        concerts.add(Concert.builder()
                .concertId(100L)
                .title("짱구는 못말려 극장판 9기 어른제국의 역습")
                .concertDate("2024-07-05 19:30")
                .description("개꿀잼")
                .price(15000L)
                .seats(50L)
                .build());
        concerts.add(Concert.builder()
                .concertId(100L)
                .title("짱구는 못말려 극장판 9기 어른제국의 역습")
                .concertDate("2024-07-06 20:30")
                .description("개꿀잼")
                .price(15000L)
                .seats(50L)
                .build());
        return concerts;
    }
}
