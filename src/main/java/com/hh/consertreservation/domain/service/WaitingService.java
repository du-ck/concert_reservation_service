package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.Token;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WaitingService {
    public Optional<Token> issued(long userId) {
        return Optional.empty();
    }
}
