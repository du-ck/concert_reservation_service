package com.hh.consertreservation.domain.concert;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class ConcertTitle {

    private Long concertId;
    private String title;

}
