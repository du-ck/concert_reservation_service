package com.hh.consertreservation.infra.cash;

import com.hh.consertreservation.domain.cash.UserBalance;
import com.hh.consertreservation.domain.cash.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserBalanceRepositoryImpl implements UserBalanceRepository {

    private final UserBalanceJpaRepository jpaRepository;

    @Override
    public Optional<UserBalance> findByUserId(long userId) {
        Optional<UserBalanceEntity> entity = jpaRepository.findByUserId(userId);
        if (entity.isPresent()) {
            return Optional.of(UserBalanceEntity.toDomain(entity.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserBalance> save(UserBalance balance) {
        Optional<UserBalanceEntity> entity = Optional.of(jpaRepository.save(UserBalanceEntity.toEntity(balance)));
        if (entity.isPresent()) {
            return Optional.of(UserBalanceEntity.toDomain(entity.get()));
        }
        return Optional.empty();
    }
}
