package com.movie.paymentservice.consumer;

import com.movie.paymentservice.producer.PaymentProducer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentConsumer {
    private final PaymentProducer paymentProducer;
    private final Random random = new Random();

    public PaymentConsumer(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }

    @KafkaListener(topics = "BOOKING_CREATED", groupId = "payment-group")
    public void handleBookingCreated(String message) {
        String bookingId = message.split(",")[0];
        System.out.println("Payment Consumer: Processing " + bookingId);

        // Random logic
        if (random.nextInt(10) < 8) {
            paymentProducer.sendPaymentCompleted(bookingId);
        } else {
            paymentProducer.sendBookingFailed(bookingId);
        }
    }
}
