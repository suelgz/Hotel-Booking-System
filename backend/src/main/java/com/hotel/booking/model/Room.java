package com.hotel.booking.model;

import com.hotel.booking.exception.InvalidRoomDataException;

public class Room {
    private String roomNumber;
    private int capacity;
    private double price;
    private boolean isAvailable;

    // API için eklendi - frontend bu field'ları bekliyor
    private Long roomId;
    private String type;
    private String status;

    public Room() {}

    public Room(String roomNumber, int capacity, double price, boolean isAvailable) {
        try {
            if (price < 0) {
                throw new InvalidRoomDataException("Price can't be negative");
            }
            this.roomNumber = roomNumber;
            this.capacity = capacity;
            this.price = price;
            this.isAvailable = isAvailable;
            this.status = isAvailable ? "Available" : "Occupied";
        } catch (InvalidRoomDataException e) {
            System.out.println("Room creation error: " + e.getMessage());
        }
    }

    public Room(String roomNumber, int capacity, double price) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.price = price;
        this.isAvailable = true;
        this.status = "Available";
    }

    public double calculatePrice(int numberOfNights) {
        return price * numberOfNights;
    }

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

    public void printAvailable() {
        if (isAvailable) {
            System.out.println("Room is available");
        } else {
            System.out.println("Room is not available");
        }
    }

    // --- API için eklendi ---
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status == null || status.isBlank() ? "Available" : status;
        this.isAvailable = "Available".equals(this.status);
    }

    // frontend "pricePerNight" bekliyor, orijinal field "price" - bu getter köprü kuruyor
    public double getPricePerNight() { return price; }
    public void setPricePerNight(double pricePerNight) { this.price = pricePerNight; }
}
