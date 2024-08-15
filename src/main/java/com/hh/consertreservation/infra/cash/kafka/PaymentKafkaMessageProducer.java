package com.hh.consertreservation.infra.cash.kafka;

import com.hh.consertreservation.domain.cash.PaymentKafkaPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentKafkaMessageProducer implements PaymentKafkaPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String topic = "paymentinfo";

    @Override
    public CompletableFuture<SendResult<String, String>> send(String message) {
         return kafkaTemplate.send(topic, message);
    }

    @Override
    public CompletableFuture<SendResult<String, String>> sendWithTopic(String topic, String message) {
        return kafkaTemplate.send(topic, message);
    }
}
