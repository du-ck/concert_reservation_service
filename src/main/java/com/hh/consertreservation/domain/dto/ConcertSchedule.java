package com.hh.consertreservation.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class ConcertSchedule {
    private Long scheduleId;
    private Long concertId;
    private String title;
    private String concertDate;
    private String description;
    private Long price;
    private Long seats;
}
