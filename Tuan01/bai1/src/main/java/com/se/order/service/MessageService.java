package com.se.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.hello}")
    private String queueName;

    public String getMessage() {
        try {
            Object message = rabbitTemplate.receiveAndConvert(queueName, 5000);
            if (message != null) {
                log.info("📤 GET: Retrieved message: {}", message);
                return message.toString();
            } else {
                log.info("Queue is empty");
                return null;
            }
        } catch (Exception e) {
            log.error("Error retrieving message", e);
            return null;
        }
    }
}
