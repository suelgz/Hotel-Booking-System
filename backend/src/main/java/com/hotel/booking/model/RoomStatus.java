package com.hotel.booking.model;

import java.util.Arrays;

public enum RoomStatus {
    AVAILABLE("Available"),
    BOOKED("Booked"),
    OCCUPIED("Occupied"),
    CLEANING("Cleaning"),
    MAINTENANCE("Maintenance");

    private final String displayName;

    RoomStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean blocksBooking() {
        return this == CLEANING || this == MAINTENANCE;
    }

    public static RoomStatus fromDisplayName(String value) {
        if (value == null || value.isBlank()) {
            return AVAILABLE;
        }
        String cleaned = value.trim();
        return Arrays.stream(values())
                .filter(status -> status.displayName.equalsIgnoreCase(cleaned)
                        || status.name().equalsIgnoreCase(cleaned))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Room status must be Available, Booked, Occupied, Cleaning, or Maintenance."
                ));
    }
}
