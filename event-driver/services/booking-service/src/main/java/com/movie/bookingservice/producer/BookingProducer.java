package com.movie.bookingservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public BookingProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookingCreated(String bookingId, Long movieId, String userId) {
        String message = bookingId + "," + movieId + "," + userId;
        kafkaTemplate.send("BOOKING_CREATED", message);
    }
}
