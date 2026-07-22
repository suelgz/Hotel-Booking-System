package com.hotel.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomRequest(
        @NotBlank(message = "Room number is required.")
        String roomNumber,

        @NotBlank(message = "Room type is required.")
        String type,

        @NotNull(message = "Room capacity is required.")
        @Min(value = 1, message = "Room capacity must be at least 1.")
        Integer capacity,

        Double pricePerNight,

        String status
) {
}
