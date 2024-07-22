package com.hh.consertreservation.infra.waiting;

import com.hh.consertreservation.domain.waiting.Token;
import com.hh.consertreservation.domain.waiting.WaitingType;
import com.hh.consertreservation.domain.waiting.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Token> getOngoingToken(long userId, String token) {
        Optional<WaitingEntity> entity = jpaRepository.findByUserIdAndTokenAndStatus(userId, token, WaitingType.ONGOING);
        if (entity.isPresent()) {
            return Optional.of(WaitingEntity.toDomain(entity.get()));
        }
        return Optional.empty();
    }

    @Override
    public int expireTokenById(long userId, String token) {

        int expireCount = jpaRepository.expireTokenById(userId, token);

        return expireCount;
    }
}
