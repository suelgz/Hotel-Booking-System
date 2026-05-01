package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation implements Bookable, Cancelable {

    private String reservationId;
    private Customer customer;
    private Room room;
    private double totalPrice;
    private boolean isBooked;
    private boolean isCancelled;
    private Payment payment;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // API için eklendi - frontend status gösteriyor
    private String status;

    public Reservation() {}

    public Reservation(String reservationId, Customer customer, Room room,
                       LocalDate checkInDate, LocalDate checkOutDate) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.isBooked = false;
        this.isCancelled = false;
        this.status = "Active";
    }

    public String getReservationId() { return reservationId; }
    @JsonIgnore
    public Customer getCustomer() { return customer; }
    @JsonIgnore
    public Room getRoom() { return room; }
    public double getTotalPrice() { return totalPrice; }
    public Payment getPayment() { return payment; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    @Override
    public void book() {
        if (!isBooked && !isCancelled) {
            this.isBooked = true;
            room.setStatus("Booked");
            int nights = (int) getNumberOfNights();
            this.totalPrice = room.calculatePrice(nights);
            this.status = "Active";

            System.out.println("Reservation " + reservationId + " booked!");
            System.out.println("Customer: " + customer.getName());
            System.out.println("Room: " + room.getRoomNumber());
            System.out.println("Check-in: " + checkInDate);
            System.out.println("Check-out: " + checkOutDate);
            System.out.println("Nights: " + nights);
            System.out.println("Total: " + totalPrice);
            customer.addReservation(this);
        } else {
            System.out.println("You can't book the room, it is not available!");
        }
    }

    @Override
    public boolean isBooked() { return isBooked; }

    @Override
    public boolean isCancelled() { return isCancelled; }

    @Override
    public void cancel() {
        if (isBooked && !isCancelled) {
            this.isCancelled = true;
            this.isBooked = false;
            this.status = "Cancelled";
            System.out.println("Reservation " + reservationId + " cancelled!");
        } else {
            System.out.println("Cannot cancel - not booked");
        }
    }

    public void makePayment(Payment payment) {
        if (payment.processPayment()) {
            this.payment = payment;
            payment.completePayment();
            System.out.println("Payment completed for reservation " + reservationId);
        } else {
            System.out.println("Payment failed");
        }
    }

    // --- API için eklendi - frontend flat field'lar bekliyor ---
    public String getCustomerName() {
        return customer != null ? customer.getFullName() : "";
    }

    public Long getCustomerId() {
        return customer != null ? customer.getCustomerId() : null;
    }

    public String getRoomNumber() {
        return room != null ? room.getRoomNumber() : "";
    }

    public Long getRoomId() {
        return room != null ? room.getRoomId() : null;
    }
}
