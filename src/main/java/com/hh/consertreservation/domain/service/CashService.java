package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.ReservationInfo;
import com.hh.consertreservation.domain.dto.UserBalance;
import com.hh.consertreservation.domain.dto.servicerequest.PaymentServiceRequestDto;
import com.hh.consertreservation.domain.repository.UserBalanceRepository;
import com.hh.consertreservation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashService {

    private final UserBalanceRepository userBalanceRepository;

    public Optional<UserBalance> getUserBalance(long userId) throws Exception {
        return Optional.empty();
    }

    @Transactional
    public Optional<UserBalance> charge(long userId, long amount) throws Exception {
        return Optional.empty();
    }

    public Optional<ReservationInfo> payment(PaymentServiceRequestDto req) {
        return Optional.empty();
    }
}
