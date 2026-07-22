package com.hotel.booking.model;

public class CashPayment extends Payment {

    private double cashReceived;
    private double change;

    public CashPayment(double amount, String paymentId, String paymentDate, double cashReceived) {
        super(amount, paymentId, paymentDate);
        this.cashReceived = cashReceived;
    }

    public double getCashReceived() { return cashReceived; }
    public double getChange() { return change; }

    @Override
    public boolean processPayment() {
        if (getAmount() <= 0 || cashReceived <= 0 || cashReceived < getAmount()) {
            return false;
        }
        change = cashReceived - getAmount();
        return true;
    }
}
