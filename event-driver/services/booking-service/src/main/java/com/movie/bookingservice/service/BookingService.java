package com.movie.bookingservice.service;

import com.movie.bookingservice.entity.Booking;
import com.movie.bookingservice.producer.BookingProducer;
import com.movie.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingProducer bookingProducer;

    public BookingService(BookingRepository bookingRepository, BookingProducer bookingProducer) {
        this.bookingRepository = bookingRepository;
        this.bookingProducer = bookingProducer;
    }

    public Booking createBooking(Long movieId, String userId) {
        String bookingId = "B" + System.currentTimeMillis();
        Booking booking = new Booking(bookingId, movieId, userId, "PENDING");
        
        Booking savedBooking = bookingRepository.save(booking);
        bookingProducer.sendBookingCreated(bookingId, movieId, userId);
        
        return savedBooking;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void updateStatus(String bookingId, String status) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            booking.setStatus(status);
            bookingRepository.save(booking);
        });
    }
}
