package com.hh.consertreservation;

import com.hh.consertreservation.domain.waiting.Token;
import com.hh.consertreservation.domain.waiting.WaitingType;
import com.hh.consertreservation.domain.waiting.WaitingService;
import com.hh.consertreservation.infra.waiting.WaitingEntity;
import com.hh.consertreservation.infra.waiting.WaitingJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@EnableScheduling
public class WaitingIntegrationTest {

    @Autowired
    WaitingService waitingService;

    @Autowired
    WaitingJpaRepository waitingRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    @Test
    void 토큰만료_스케쥴러_테스트() throws Exception {
        UUID uuid = UUID.randomUUID();
        WaitingType status = WaitingType.ONGOING;

        WaitingEntity waitingEntity = WaitingEntity.builder()
                .userId(userId)
                .token(uuid.toString())
                .status(status)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().minusMinutes(1)) //만료시간이 1분지난 토큰 생성
                .build();
        Optional<WaitingEntity> saveResult = Optional.of(waitingRepository.save(waitingEntity));
        //만료테스트를 위해 저장한 대기열토큰
        Token token = WaitingEntity.toDomain(saveResult.get());

        Thread.sleep(8000); // 스케쥴러가 도는 시간을 대기

        //추가했던 토큰 조회
        Optional<WaitingEntity> expireResult = waitingRepository.findById(token.getId());

        Assertions.assertEquals(token.getQueueToken(), expireResult.get().getToken());
        Assertions.assertEquals(WaitingType.EXPIRE, expireResult.get().getStatus());
    }
}
