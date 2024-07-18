package com.hh.consertreservation.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUser(long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserWithLock(long userId) {
        return userRepository.findByIdWithLock(userId);
    }
}
