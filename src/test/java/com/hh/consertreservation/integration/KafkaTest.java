package com.hh.consertreservation.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@SpringBootTest
@Slf4j
public class KafkaTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private String receivedMessage;

    @Test
    void kafkaTest() throws Exception {
        String sendMessage = "hihihi";

        CompletableFuture<SendResult<String, String>> future= kafkaTemplate.send("kafkaTest", sendMessage);

        // 잠시 대기 후 결과 검증
        Thread.sleep(1000);

        // 메시지가 정상적으로 전송되었는지 확인
        Assertions.assertEquals(receivedMessage, sendMessage);
    }

    @KafkaListener(topics = "kafkaTest", groupId = "testGroup1")
    public void listen(String message) {
        log.info("listen!!");
        receivedMessage = message;
    }
}
