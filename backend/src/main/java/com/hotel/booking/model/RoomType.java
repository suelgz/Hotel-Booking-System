package com.hotel.booking.model;

public enum RoomType {
    SINGLE("Single", 100.0),
    DOUBLE("Double", 120.0),
    SUITE("Suite", 300.0);

    private final String displayName;
    private final double pricePerNight;

    RoomType(String displayName, double pricePerNight) {
        this.displayName = displayName;
        this.pricePerNight = pricePerNight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public static RoomType fromDisplayName(String value) {
        if (value == null || value.isBlank()) {
            return SINGLE;
        }
        for (RoomType type : values()) {
            if (type.displayName.equalsIgnoreCase(value.trim())
                    || type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Room type must be Single, Double, or Suite.");
    }

    public static RoomType fromCapacity(int capacity) {
        return capacity <= 2 ? SINGLE : DOUBLE;
    }
}
