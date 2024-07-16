package com.hh.consertreservation.infra.user;

import com.hh.consertreservation.domain.user.User;
import com.hh.consertreservation.domain.user.UserRepository;
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
