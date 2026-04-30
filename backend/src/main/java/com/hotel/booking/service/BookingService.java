package com.hotel.booking.service;

import com.hotel.booking.exception.InvalidCustomerDataException;
import com.hotel.booking.exception.RoomNotAvailableException;
import com.hotel.booking.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Service
public class BookingService {

    private final List<Room> rooms = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

    private final AtomicLong roomIdCounter = new AtomicLong(1);
    private final AtomicLong customerIdCounter = new AtomicLong(1);
    private int reservationCounter = 1;

    public BookingService() {
        StandardRoom r1 = new StandardRoom("101", 2, 100.0, true);
        r1.setRoomId(roomIdCounter.getAndIncrement());
        rooms.add(r1);

        StandardRoom r2 = new StandardRoom("102", 2, 100.0, true);
        r2.setRoomId(roomIdCounter.getAndIncrement());
        rooms.add(r2);

        StandardRoom r3 = new StandardRoom("103", 4, 120.0, true);
        r3.setType("Double");
        r3.setRoomId(roomIdCounter.getAndIncrement());
        rooms.add(r3);

        SuiteRoom s1 = new SuiteRoom("201", 2, 250.0, true, 50, "Deluxe", true);
        s1.setRoomId(roomIdCounter.getAndIncrement());
        rooms.add(s1);

        SuiteRoom s2 = new SuiteRoom("202", 6, 300.0, true, 75, "Presidential", true);
        s2.setRoomId(roomIdCounter.getAndIncrement());
        rooms.add(s2);
    }

    // ─── Rooms

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public Room addRoom(Room newRoom) {
        newRoom.setRoomId(roomIdCounter.getAndIncrement());
        if (newRoom.getStatus() == null) newRoom.setStatus("Available");
        rooms.add(newRoom);
        return newRoom;
    }

    public Room updateRoom(Long roomId, Room updated) {
        Room room = findRoomById(roomId);
        if (room == null) return null;
        room.setRoomNumber(updated.getRoomNumber());
        room.setType(updated.getType());
        room.setCapacity(updated.getCapacity());
        room.setPrice(updated.getPricePerNight());
        room.setStatus(updated.getStatus());
        return room;
    }

    public boolean deleteRoom(Long roomId) {
        return rooms.removeIf(r -> r.getRoomId().equals(roomId));
    }

    // ─── Customers 

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer addCustomer(String name, String surname, String email, String phone)
            throws InvalidCustomerDataException {
        Customer customer = new Customer(name, surname, email, phone);
        customer.setCustomerId(customerIdCounter.getAndIncrement());
        customers.add(customer);
        return customer;
    }

    public Customer updateCustomer(Long customerId, String name, String surname,
                                   String email, String phone) {
        Customer customer = findCustomerById(customerId);
        if (customer == null) return null;
        if (name != null) customer.setName(name);
        if (surname != null) customer.setSurname(surname);
        if (email != null) customer.setEmail(email);
        if (phone != null) customer.setPhone(phone);
        return customer;
    }

    public boolean deleteCustomer(Long customerId) {
        return customers.removeIf(c -> c.getCustomerId().equals(customerId));
    }

    // ─── Reservations 

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public Reservation createReservation(String customerName, String roomNumber,
                                         LocalDate checkIn, LocalDate checkOut)
            throws RoomNotAvailableException, InvalidCustomerDataException {

        Room room = rooms.stream()
                .filter(r -> r.getRoomNumber().equals(roomNumber))
                .findFirst()
                .orElseThrow(() -> new RoomNotAvailableException("Room not found: " + roomNumber));

       
        if (!room.isAvailable()) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not available.");
        }

        Customer customer = customers.stream()
                .filter(c -> c.getFullName().equalsIgnoreCase(customerName))
                .findFirst()
                .orElse(null);

        if (customer == null) {
            String[] parts = customerName.trim().split(" ", 2);
            String first = parts[0];
            String last = parts.length > 1 ? parts[1] : "Guest";
            customer = new Customer(first, last, "guest@hotel.com", "0000000000");
            customer.setCustomerId(customerIdCounter.getAndIncrement());
            customers.add(customer);
        }

        String resId = String.format("RES-%03d", reservationCounter++);
        Reservation reservation = new Reservation(resId, customer, room, checkIn, checkOut);

       
        reservation.book();

        reservations.add(reservation);
        return reservation;
    }

    
    public boolean cancelReservation(String reservationId) {
        Reservation reservation = reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst()
                .orElse(null);
        if (reservation == null) return false;
        reservation.cancel();
        return true;
    }

    // ─── Helpers 

    private Room findRoomById(Long id) {
        return rooms.stream().filter(r -> r.getRoomId().equals(id)).findFirst().orElse(null);
    }

    private Customer findCustomerById(Long id) {
        return customers.stream().filter(c -> c.getCustomerId().equals(id)).findFirst().orElse(null);
    }
}
