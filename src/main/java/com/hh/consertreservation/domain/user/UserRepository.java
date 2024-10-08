package com.hh.consertreservation.domain.user;


import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long userId);
    Optional<User> findByIdWithLock(long userId);
}
