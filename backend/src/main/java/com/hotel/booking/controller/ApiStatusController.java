package com.hotel.booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiStatusController {

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of("message", "Hotel Booking API is running");
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "hotel-booking-api");
    }
}
