package com.hotel.booking.model;

public class CreditCardPayment extends Payment {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;

    public CreditCardPayment(double amount, String paymentId, String paymentDate,
                             String cardNumber, String cardHolderName, String expiryDate) {
        super(amount, paymentId, paymentDate);
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean processPayment() {
        if (cardNumber == null || cardNumber.length() < 16) {
            return false;
        }
        return cardHolderName != null
                && !cardHolderName.matches(".*\\d.*")
                && expiryDate != null
                && !expiryDate.isBlank();
    }

    public String getCardNumber() { return cardNumber; }
    public String getCardHolderName() { return cardHolderName; }
    public String getExpiryDate() { return expiryDate; }
}
