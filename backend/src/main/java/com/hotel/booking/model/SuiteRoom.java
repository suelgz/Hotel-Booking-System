package com.hotel.booking.model;

public class SuiteRoom extends Room {

    private int luxuryFee;
    private String suiteLevel;
    private boolean hasLivingRoom;

    public SuiteRoom(String roomNumber, int capacity, double price, boolean isAvailable,
                     int luxuryFee, String suiteLevel, boolean hasLivingRoom) {
        super(roomNumber, capacity, price, isAvailable);
        this.luxuryFee = luxuryFee;
        this.suiteLevel = suiteLevel;
        this.hasLivingRoom = hasLivingRoom;
        setType("Suite");
    }

    public double getLuxuryFee() { return luxuryFee; }
    public void setLuxuryFee(int luxuryFee) { this.luxuryFee = luxuryFee; }

    public String getSuiteLevel() { return suiteLevel; }
    public void setSuiteLevel(String suiteLevel) { this.suiteLevel = suiteLevel; }

    public boolean isHasLivingRoom() { return hasLivingRoom; }
    public void setHasLivingRoom(boolean hasLivingRoom) { this.hasLivingRoom = hasLivingRoom; }

    @Override
    public void printAvailable() {
        System.out.println("Suite Room availability: " + isAvailable());
    }

    @Override
    public double calculatePrice(int numberOfNights) {
        double totalPrice = (getPrice() + luxuryFee) * numberOfNights;
        return totalPrice;
    }

    @Override
    public String toString() {
        return "SuiteRoom " + getRoomNumber() + " - " + suiteLevel + " Suite, Price: " + getPrice()
                + ", Luxury Fee: $" + luxuryFee + ", Living Room: " + (hasLivingRoom ? "Yes" : "No")
                + ", Available: " + (isAvailable() ? "Yes" : "No");
    }
}
