package com.hh.consertreservation.infra.impl;

import com.hh.consertreservation.domain.dto.User;
import com.hh.consertreservation.domain.repository.UserRepository;
import com.hh.consertreservation.infra.entity.UserEntity;
import com.hh.consertreservation.infra.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findById(long userId) {
        Optional<UserEntity> userEntity = jpaRepository.findById(userId);
        if (userEntity.isPresent()) {
            return Optional.of(UserEntity.toDomain(userEntity.get()));
        }
        return Optional.empty();
    }
}
