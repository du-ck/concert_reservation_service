package com.hh.consertreservation.interfaces.concert;

import com.hh.consertreservation.domain.concert.Concert;
import com.hh.consertreservation.domain.concert.ConcertTitle;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

public class Concerts {

    public static class Request {

    }

    @Builder
    @Getter
    public static class Response {
        List<ConcertTitle> concerts;
    }
}
