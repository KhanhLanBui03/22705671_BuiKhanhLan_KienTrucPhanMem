package com.movie.paymentservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentCompleted(String bookingId) {
        kafkaTemplate.send("PAYMENT_COMPLETED", bookingId);
    }

    public void sendBookingFailed(String bookingId) {
        kafkaTemplate.send("BOOKING_FAILED", bookingId);
    }
}
