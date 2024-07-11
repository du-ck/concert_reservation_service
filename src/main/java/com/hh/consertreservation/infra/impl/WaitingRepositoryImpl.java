package com.hh.consertreservation.infra.impl;

import com.hh.consertreservation.domain.dto.Token;
import com.hh.consertreservation.domain.dto.types.WaitingType;
import com.hh.consertreservation.domain.repository.WaitingRepository;
import com.hh.consertreservation.infra.entity.WaitingEntity;
import com.hh.consertreservation.infra.jpa.WaitingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WaitingRepositoryImpl implements WaitingRepository {

    private final WaitingJpaRepository jpaRepository;

    @Override
    public int expire() {
        int expireCount = jpaRepository.expireToken(LocalDateTime.now());
        return expireCount;
    }

    @Override
    public Optional<Token> addToken(long userId, String uuid, WaitingType status) {
        WaitingEntity waitingEntity = WaitingEntity.builder()
                .userId(userId)
                .token(uuid)
                .status(status)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        Optional<WaitingEntity> saveResult = Optional.of(jpaRepository.save(waitingEntity));

        if (saveResult.isPresent()) {
            Optional<Token> token = Optional.of(WaitingEntity.toDomain(saveResult.get()));
            return token;
        }
        return Optional.empty();
    }

    @Override
    public int getOnGoingCount() {
        return jpaRepository.countByStatus(WaitingType.ONGOING);
    }

    @Override
    public List<Token> getNextWaiting(int limit) {
        List<WaitingEntity> nextWaitings = jpaRepository.findWaitingWithLimit(limit);
        if (!CollectionUtils.isEmpty(nextWaitings)) {
            return WaitingEntity.toDomainList(nextWaitings);
        }
        return new ArrayList<>();
    }

    @Override
    public int updateTokenOngoing(List<Token> waitingTokens) {
        List<WaitingEntity> saveResult = jpaRepository.saveAll(WaitingEntity.toEntityList(waitingTokens));
        return saveResult.size();
    }
}
