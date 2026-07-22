package com.hotel.booking.model;

public class Room {
    private String roomNumber;
    private int capacity;
    private double price;
    private boolean isAvailable;
    private Long roomId;
    private String type;
    private String status;

    public Room() {}

    public Room(String roomNumber, int capacity, double price, boolean isAvailable) {
        validateInitialState(roomNumber, capacity, price);
        this.roomNumber = roomNumber.trim();
        this.capacity = capacity;
        this.price = price;
        this.isAvailable = isAvailable;
        this.status = isAvailable ? "Available" : "Occupied";
    }

    public Room(String roomNumber, int capacity, double price) {
        this(roomNumber, capacity, price, true);
    }

    public double calculatePrice(int numberOfNights) {
        return price * numberOfNights;
    }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        validatePrice(price);
        this.price = price;
    }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) {
        isAvailable = available;
        if (!"Maintenance".equals(this.status) && !"Cleaning".equals(this.status)) {
            this.status = available ? "Available" : "Occupied";
        }
    }

    public void printAvailable() {
        if (isAvailable) {
            System.out.println("Room is available");
        } else {
            System.out.println("Room is not available");
        }
    }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status == null || status.isBlank() ? "Available" : status;
        this.isAvailable = "Available".equals(this.status);
    }

    public double getPricePerNight() { return price; }
    public void setPricePerNight(double pricePerNight) { setPrice(pricePerNight); }

    private void validateInitialState(String roomNumber, int capacity, double price) {
        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException("Room number is required.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Room capacity must be at least 1.");
        }
        validatePrice(price);
    }

    private void validatePrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price per night cannot be negative.");
        }
    }
}
