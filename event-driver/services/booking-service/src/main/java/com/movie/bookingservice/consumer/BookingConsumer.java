package com.movie.bookingservice.consumer;

import com.movie.bookingservice.service.BookingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingConsumer {
    private final BookingService bookingService;

    public BookingConsumer(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = "PAYMENT_COMPLETED", groupId = "booking-group")
    public void handlePaymentCompleted(String bookingId) {
        System.out.println("Consumer: Payment completed for " + bookingId);
        bookingService.updateStatus(bookingId, "SUCCESS");
    }

    @KafkaListener(topics = "BOOKING_FAILED", groupId = "booking-group")
    public void handleBookingFailed(String bookingId) {
        System.out.println("Consumer: Booking failed for " + bookingId);
        bookingService.updateStatus(bookingId, "FAILED");
    }
}
