package com.hh.consertreservation.domain.concert;


import java.util.List;

public interface ConcertRepository {
    List<ConcertTitle> findAll();
}
