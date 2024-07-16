package com.hh.consertreservation.domain.service;

import com.hh.consertreservation.domain.waiting.Token;
import com.hh.consertreservation.domain.waiting.WaitingService;
import com.hh.consertreservation.domain.waiting.WaitingType;
import com.hh.consertreservation.domain.waiting.WaitingRepository;
import com.hh.consertreservation.exception.TokenIssuedException;
import com.hh.consertreservation.exception.TokenVerificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class WaitingServiceTest {

    @InjectMocks
    WaitingService waitingService;

    @Mock
    private WaitingRepository waitingRepository;

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
        Token token = Token.builder()
                .id(1L)
                .userId(userId)
                .queueToken(uuid.toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .status(WaitingType.WAITING)
                .build();

        given(waitingRepository.getOnGoingCount())
                .willReturn(100);
        given(waitingRepository.addToken(anyLong(), anyString(), any(WaitingType.class)))
                .willReturn(Optional.of(token));

        Optional<Token> issuedToken = waitingService.issued(userId, max_ongoing_count);

        Assertions.assertEquals(uuid.toString(), issuedToken.get().getQueueToken());
        Assertions.assertEquals(userId, issuedToken.get().getUserId());
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
        Token token = Token.builder()
                .id(1L)
                .userId(userId)
                .queueToken(uuid.toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .status(WaitingType.ONGOING)
                .build();
        given(waitingRepository.getOngoingToken(userId, uuid.toString()))
                .willReturn(Optional.of(token));

        boolean result = waitingService.verification(userId, uuid.toString());

        Assertions.assertEquals(result, true);
    }

    @Test
    void 토큰인증_실패() {
        UUID uuid = UUID.randomUUID();
        Token token = Token.builder()
                .id(1L)
                .userId(userId)
                .queueToken(uuid.toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .status(WaitingType.ONGOING)
                .build();
        given(waitingRepository.getOngoingToken(userId, uuid.toString()))
                .willReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(TokenVerificationException.class,
                () -> waitingService.verification(userId, uuid.toString()));
        Assertions.assertEquals("토큰인증 실패", exception.getMessage());
    }
}