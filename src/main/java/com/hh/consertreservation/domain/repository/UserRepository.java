package com.hh.consertreservation.domain.repository;


import com.hh.consertreservation.domain.dto.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long userId);
}
