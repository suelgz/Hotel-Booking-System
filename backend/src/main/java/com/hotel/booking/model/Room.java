package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Room {
    private String roomNumber;
    private int capacity;
    private double price;
    private Long roomId;
    private RoomType type = RoomType.SINGLE;
    private RoomStatus status = RoomStatus.AVAILABLE;

    public Room() {}

    public Room(String roomNumber, int capacity, double price, boolean isAvailable) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.price = price;
        this.status = isAvailable ? RoomStatus.AVAILABLE : RoomStatus.OCCUPIED;
    }

    public Room(String roomNumber, int capacity, double price) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.price = price;
        this.status = RoomStatus.AVAILABLE;
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

    public boolean isAvailable() { return status == RoomStatus.AVAILABLE; }
    public void setAvailable(boolean available) {
        if (status != RoomStatus.MAINTENANCE && status != RoomStatus.CLEANING) {
            this.status = available ? RoomStatus.AVAILABLE : RoomStatus.OCCUPIED;
        }
    }

    public void printAvailable() {}

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getType() { return type.getDisplayName(); }
    public void setType(String type) { this.type = RoomType.fromDisplayName(type); }
    @JsonIgnore
    public RoomType getRoomType() { return type; }
    public void setRoomType(RoomType type) { this.type = type == null ? RoomType.SINGLE : type; }

    public String getStatus() { return status.getDisplayName(); }
    public void setStatus(String status) { this.status = RoomStatus.fromDisplayName(status); }
    @JsonIgnore
    public RoomStatus getRoomStatus() { return status; }
    public void setRoomStatus(RoomStatus status) { this.status = status == null ? RoomStatus.AVAILABLE : status; }

    public double getPricePerNight() { return price; }
    public void setPricePerNight(double pricePerNight) { this.price = pricePerNight; }
}
