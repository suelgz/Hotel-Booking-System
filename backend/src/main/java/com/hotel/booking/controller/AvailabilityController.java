package com.hotel.booking.controller;

import com.hotel.booking.exception.InvalidReservationDataException;
import com.hotel.booking.service.BookingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
    private final BookingService bookingService;

    public AvailabilityController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public Map<String, Object> getAvailability(
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate
    ) throws InvalidReservationDataException {
        return bookingService.getAvailability(
                LocalDate.parse(checkInDate),
                LocalDate.parse(checkOutDate)
        );
    }
}
