package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation implements Bookable, Cancelable {

    private String reservationId;
    private Customer customer;
    private Room room;
    private double totalPrice;
    private Payment payment;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status = ReservationStatus.ACTIVE;

    public Reservation() {}

    public Reservation(String reservationId, Customer customer, Room room,
                       LocalDate checkInDate, LocalDate checkOutDate) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.ACTIVE;
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
    public String getStatus() { return getEffectiveStatus().getDisplayName(); }
    public void setStatus(String status) { this.status = ReservationStatus.fromDisplayName(status); }
    @JsonIgnore
    public ReservationStatus getReservationStatus() { return getEffectiveStatus(); }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    @Override
    public void book() {
        if (status != ReservationStatus.CANCELLED) {
            int nights = (int) getNumberOfNights();
            this.totalPrice = room.calculatePrice(nights);
            this.status = ReservationStatus.ACTIVE;
            customer.addReservation(this);
        }
    }

    @Override
    public boolean isBooked() { return getEffectiveStatus() == ReservationStatus.ACTIVE; }

    @Override
    public boolean isCancelled() { return status == ReservationStatus.CANCELLED; }

    @Override
    public void cancel() {
        if (getEffectiveStatus() == ReservationStatus.ACTIVE) {
            this.status = ReservationStatus.CANCELLED;
        }
    }

    public void makePayment(Payment payment) {
        if (payment.processPayment()) {
            this.payment = payment;
            payment.completePayment();
        }
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

    public boolean overlaps(LocalDate checkIn, LocalDate checkOut) {
        return checkInDate.isBefore(checkOut) && checkIn.isBefore(checkOutDate);
    }

    public boolean isCurrentStay(LocalDate date) {
        return getEffectiveStatus() == ReservationStatus.ACTIVE
                && !checkInDate.isAfter(date)
                && checkOutDate.isAfter(date);
    }

    public boolean isUpcoming(LocalDate date) {
        return getEffectiveStatus() == ReservationStatus.ACTIVE && checkInDate.isAfter(date);
    }

    private ReservationStatus getEffectiveStatus() {
        if (status == ReservationStatus.ACTIVE
                && checkOutDate != null
                && !LocalDate.now().isBefore(checkOutDate)) {
            return ReservationStatus.COMPLETED;
        }
        return status;
    }
}
