package com.hotel.booking.model;


public class Room {

    private Long roomId;
    private String roomNumber;
    private int capacity;
    private double price;
    private boolean isAvailable;
    private String type;           // "Single" | "Double" | "Suite"
    private String status;         // "Available" | "Occupied" | "Maintenance"

    public Room() {}

    public Room(String roomNumber, int capacity, double price, boolean isAvailable) {
        if (price < 0) {
            throw new IllegalArgumentException("Price can't be negative");
        }
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.price = price;
        this.isAvailable = isAvailable;
        this.status = isAvailable ? "Available" : "Occupied";
    }


    public double calculatePrice(int numberOfNights) {
        return price * numberOfNights;
    }

    // ── Getters & Setters

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) {
        isAvailable = available;

        if (!"Maintenance".equals(this.status)) {
            this.status = available ? "Available" : "Occupied";
        }
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.isAvailable = "Available".equals(status);
    }


    public double getPricePerNight() { return price; }
}