package com.hotel.booking.model;

public class StandardRoom extends Room {

    public StandardRoom(String roomNumber, int capacity, double price, boolean isAvailable) {
        super(roomNumber, capacity, price, isAvailable);
        setRoomType(RoomType.fromCapacity(capacity));
    }

    @Override
    public void printAvailable() {}

    @Override
    public double calculatePrice(int numberOfNights) {
        return getPrice() * numberOfNights;
    }

    @Override
    public String toString() {
        return "StandardRoom - Room: " + getRoomNumber() + ", Price: " + getPrice() + ", Available: " + isAvailable();
    }
}
