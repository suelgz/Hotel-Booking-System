package com.hotel.booking.model;

public enum ReservationStatus {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReservationStatus fromDisplayName(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        for (ReservationStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(value.trim())
                    || status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Reservation status must be Active, Completed, or Cancelled.");
    }
}
