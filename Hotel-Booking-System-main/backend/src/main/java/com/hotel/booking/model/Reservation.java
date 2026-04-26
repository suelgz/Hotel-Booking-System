package com.hotel.booking.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {

    private String reservationId;
    private Customer customer;
    private Room room;
    private double totalPrice;
    private boolean isBooked;
    private boolean isCancelled;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;   // "Active" | "Completed" | "Cancelled" - frontend

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


    public void book() {
        if (room.isAvailable() && !isBooked && !isCancelled) {
            this.isBooked = true;
            room.setAvailable(false);
            int nights = (int) getNumberOfNights();
            this.totalPrice = room.calculatePrice(nights);
            this.status = "Active";
            customer.addReservation(this);
        }
    }


    public void cancel() {
        if (isBooked && !isCancelled) {
            this.isCancelled = true;
            this.isBooked = false;
            this.status = "Cancelled";
            room.setAvailable(true);
        }
    }

    public void complete() {
        this.status = "Completed";
    }


    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }


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

    // ── Getters & Setters

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }

    public boolean isCancelled() { return isCancelled; }
    public void setCancelled(boolean cancelled) { isCancelled = cancelled; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}