package com.movie.userservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserRegistered(String username) {
        kafkaTemplate.send("USER_REGISTERED", "User " + username + " registered successfully");
    }
}
