package com.movie.paymentservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(topics = "PAYMENT_COMPLETED", groupId = "notification-group")
    public void handleNotification(String bookingId) {
        System.out.println("NOTIFICATION: User A đã đặt đơn #" + bookingId + " thành công");
    }
}
