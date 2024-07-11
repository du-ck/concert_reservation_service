package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.Token;
import com.hh.consertreservation.domain.dto.User;
import com.hh.consertreservation.domain.dto.types.WaitingType;
import com.hh.consertreservation.domain.repository.UserRepository;
import com.hh.consertreservation.domain.repository.WaitingRepository;
import com.hh.consertreservation.exception.TokenIssuedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUser(long userId) {
        return userRepository.findById(userId);
    }
}
