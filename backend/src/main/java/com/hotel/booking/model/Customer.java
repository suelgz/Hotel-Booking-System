package com.hotel.booking.model;

import com.hotel.booking.exception.InvalidCustomerDataException;
import java.util.ArrayList;
import java.util.List;

public class Customer {

    private Long customerId;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private List<Reservation> reservations = new ArrayList<>();

    public Customer() {}

    // Orijinalindeki aynı validasyonlar - hiç değişmedi
    public Customer(String name, String surname, String email, String phone)
            throws InvalidCustomerDataException {

        if (name == null || name.isEmpty()) {
            throw new InvalidCustomerDataException("Name can't be null or empty");
        }
        if (surname == null || surname.isEmpty()) {
            throw new InvalidCustomerDataException("Surname can't be null or empty");
        }
        if (email == null || email.isEmpty()) {
            throw new InvalidCustomerDataException("Email can't be null or empty");
        }
        if (!email.contains("@")) {
            throw new InvalidCustomerDataException("Invalid email format");
        }
        if (phone == null || phone.isEmpty()) {
            throw new InvalidCustomerDataException("Phone can't be null or empty");
        }

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    public void addReservation(Reservation r) { reservations.add(r); }
    public void removeReservation(Reservation r) { reservations.remove(r); }


    public String getFullName() { return name + " " + surname; }


    public int getTotalReservations() { return reservations.size(); }

    // ── Getters & Setters

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<Reservation> getReservations() { return reservations; }
}