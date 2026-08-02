package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "reservations")
public class Reservation implements Bookable, Cancelable {

    @Id
    private String reservationId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private double totalPrice;

    @Transient
    private Payment payment;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    @JsonIgnore
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    @JsonIgnore
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public Payment getPayment() { return payment; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public String getStatus() { return getEffectiveStatus().getDisplayName(); }
    public void setStatus(String status) { this.status = ReservationStatus.fromDisplayName(status); }
    @JsonIgnore
    public ReservationStatus getReservationStatus() { return getEffectiveStatus(); }
    public void setReservationStatus(ReservationStatus status) { this.status = status == null ? ReservationStatus.ACTIVE : status; }

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
