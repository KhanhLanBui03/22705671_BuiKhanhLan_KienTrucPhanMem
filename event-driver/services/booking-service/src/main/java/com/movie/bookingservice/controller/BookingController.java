package com.movie.bookingservice.controller;

import com.movie.bookingservice.dto.BookingRequest;
import com.movie.bookingservice.entity.Booking;
import com.movie.bookingservice.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest request) {
        return bookingService.createBooking(request.getMovieId(), request.getUserId());
    }

    @GetMapping
    public List<Booking> getBookings() {
        return bookingService.getAllBookings();
    }
}
