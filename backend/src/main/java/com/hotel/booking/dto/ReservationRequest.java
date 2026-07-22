package com.hotel.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationRequest(
        @NotBlank(message = "Guest name is required.")
        String customerName,

        @NotBlank(message = "Please choose an available room.")
        String roomNumber,

        @NotNull(message = "Check-in date is required.")
        LocalDate checkInDate,

        @NotNull(message = "Check-out date is required.")
        LocalDate checkOutDate
) {
}
