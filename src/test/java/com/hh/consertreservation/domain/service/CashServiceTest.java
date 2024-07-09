package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.dto.UserBalance;
import com.hh.consertreservation.domain.repository.UserBalanceRepository;
import com.hh.consertreservation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {

    @InjectMocks
    private CashService cashService;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    /**
     * 잔액조회의 기본기능 테스트
     */
    @Test
    void 잔액조회() throws Exception {
        UserBalance mockBalance = UserBalance.builder()
                .userId(userId)
                .balance(10000L)
                .updatedAt(LocalDateTime.now())
                .build();

        given(userBalanceRepository.findByUserId(userId))
                .willReturn(Optional.of(mockBalance));

        Optional<UserBalance> balance = cashService.getUserBalance(userId);

        Assertions.assertEquals(userId, balance.get().getUserId());
        Assertions.assertEquals(10000L, balance.get().getBalance());
    }

    /**
     * 잔액조회했을때 데이터자체가 없을 경우 = 없는 사용자 취급
     */
    @Test
    void 잔액조회_결과없음() {

        given(userBalanceRepository.findByUserId(userId))
                .willReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> cashService.getUserBalance(userId));

        Assertions.assertEquals("유저를 찾을 수 없습니다", exception.getMessage());
    }

}