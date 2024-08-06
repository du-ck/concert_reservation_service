package com.hh.consertreservation.infra.concert;

import com.hh.consertreservation.domain.concert.ConcertRepository;
import com.hh.consertreservation.domain.concert.ConcertTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository jpaRepository;

    @Override
    public List<ConcertTitle> findAll() {
        List<ConcertEntity> entities = jpaRepository.findAll();
        return ConcertEntity.toDomainList(entities);
    }
}
