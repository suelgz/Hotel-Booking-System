package com.hotel.booking.model;

public class StandardRoom extends Room {

    public StandardRoom(String roomNumber, int capacity, double price, boolean isAvailable) {
        super(roomNumber, capacity, price, isAvailable);
        // capacity'ye göre otomatik type belirleniyor
        setType(capacity <= 1 ? "Single" : "Double");
    }

    @Override
    public void printAvailable() {
        System.out.println("Standard Room availability: " + isAvailable());
    }

    @Override
    public double calculatePrice(int numberOfNights) {
        return getPrice() * numberOfNights;
    }

    @Override
    public String toString() {
        return "StandardRoom - Room: " + getRoomNumber() + ", Price: " + getPrice() + ", Available: " + isAvailable();
    }
}
