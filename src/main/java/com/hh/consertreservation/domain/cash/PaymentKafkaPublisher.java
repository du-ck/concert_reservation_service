package com.hh.consertreservation.domain.cash;

import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface PaymentKafkaPublisher {
    CompletableFuture<SendResult<String, String>> send(String message);
    CompletableFuture<SendResult<String, String>> sendWithTopic(String topic, String message);
}
