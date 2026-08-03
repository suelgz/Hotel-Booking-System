package com.hotel.booking;

import com.hotel.booking.config.DatabaseEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotelBookingApplication {
    public static void main(String[] args) {
        DatabaseEnvironment.configure();
        SpringApplication.run(HotelBookingApplication.class, args);
    }
}
