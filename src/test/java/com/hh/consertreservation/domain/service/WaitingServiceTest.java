package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.common.RedisRepository;
import com.hh.consertreservation.domain.waiting.*;
import com.hh.consertreservation.support.exception.TokenIssuedException;
import com.hh.consertreservation.support.exception.TokenVerificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class WaitingServiceTest {

    @InjectMocks
    WaitingService waitingService;

    @Mock
    private WaitingRepository waitingRepository;

    @Mock
    private RedisRepository redisRepository;

    private Long userId;
    private Long max_ongoing_count;

    @BeforeEach
    void setUp() {
        userId = 1L;
        max_ongoing_count = 500L;
    }

    @Test
    void 토큰발급() throws Exception {
        UUID uuid = UUID.randomUUID();
        String tokenWithUserId = String.format("%s:%s", uuid, userId);
        Set<Object> sets = new HashSet<>();

        given(redisRepository.getActiveTokenCount())
                .willReturn(0);
        given(redisRepository.addActiveToken(Collections.singletonList(any(ActiveToken.class))))
                .willReturn(true);

        String issuedToken = waitingService.issued(userId, max_ongoing_count);
        Assertions.assertNotNull(issuedToken);
    }

    @Test
    void 발급실패() {

        given(waitingRepository.getOnGoingCount())
                .willReturn(100);
        given(waitingRepository.addToken(anyLong(), anyString(), any(WaitingType.class)))
                .willReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(TokenIssuedException.class,
                () -> waitingService.issued(userId, max_ongoing_count));

        Assertions.assertEquals("토큰 발급 실패", exception.getMessage());
    }

    @Test
    void 토큰인증() throws Exception {
        UUID uuid = UUID.randomUUID();
        given(redisRepository.existActiveToken(anyString()))
                .willReturn(true);

        boolean result = waitingService.verification(uuid.toString());

        Assertions.assertEquals(result, true);
    }

    @Test
    void 토큰인증_실패() throws Exception {
        UUID uuid = UUID.randomUUID();
        given(redisRepository.existActiveToken(anyString()))
                .willReturn(false);

        boolean result = waitingService.verification(uuid.toString());

        Exception exception = Assertions.assertThrows(TokenVerificationException.class,
                () -> waitingService.verification(uuid.toString()));
        Assertions.assertEquals("토큰인증 실패", exception.getMessage());
    }
}