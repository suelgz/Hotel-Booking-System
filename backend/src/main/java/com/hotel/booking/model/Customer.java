package com.hotel.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel.booking.exception.InvalidCustomerDataException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
    private List<Reservation> reservations = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    public Customer() {}

    public Customer(String name, String surname, String email, String phone)
            throws InvalidCustomerDataException {

        if (email == null || email.isEmpty()) {
            throw new InvalidCustomerDataException("Email can't be null or empty");
        }
        if (!email.contains("@")) {
            throw new InvalidCustomerDataException("Invalid email format");
        }
        if (phone == null || phone.isEmpty()) {
            throw new InvalidCustomerDataException("Phone can't be null or empty");
        }
        if (surname == null || surname.isEmpty()) {
            throw new InvalidCustomerDataException("Surname can't be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new InvalidCustomerDataException("Name can't be null or empty");
        }

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public void addReservation(Reservation r) { reservations.add(r); }
    public void removeReservation(Reservation r) { reservations.remove(r); }
    @JsonIgnore
    public List<Reservation> getReservations() { return reservations; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getFullName() {
        String first = name == null ? "" : name;
        String last = surname == null ? "" : surname;
        return (first + " " + last).trim();
    }

    public int getTotalReservations() { return reservations.size(); }
}
