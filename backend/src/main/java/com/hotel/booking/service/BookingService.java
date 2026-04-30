package com.hotel.booking.service;

import com.hotel.booking.exception.InvalidCustomerDataException;
import com.hotel.booking.exception.InvalidReservationDataException;
import com.hotel.booking.exception.RoomNotAvailableException;
import com.hotel.booking.model.Customer;
import com.hotel.booking.model.Reservation;
import com.hotel.booking.model.Room;
import com.hotel.booking.model.StandardRoom;
import com.hotel.booking.model.SuiteRoom;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookingService {

    private final List<Room> rooms = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

    private final AtomicLong roomIdCounter = new AtomicLong(1);
    private final AtomicLong customerIdCounter = new AtomicLong(1);
    private int reservationCounter = 1;

    public BookingService() {
        addSeedRoom(new StandardRoom("101", 2, 100.0, true));
        addSeedRoom(new StandardRoom("102", 2, 100.0, true));

        StandardRoom doubleRoom = new StandardRoom("103", 4, 120.0, true);
        doubleRoom.setType("Double");
        addSeedRoom(doubleRoom);

        addSeedRoom(new SuiteRoom("201", 2, 250.0, true, 50, "Deluxe", true));
        addSeedRoom(new SuiteRoom("202", 6, 300.0, true, 75, "Presidential", true));
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public Room addRoom(Room newRoom) {
        validateRoom(newRoom);
        newRoom.setRoomId(roomIdCounter.getAndIncrement());
        if (newRoom.getStatus() == null || newRoom.getStatus().isBlank()) {
            newRoom.setStatus("Available");
        }
        rooms.add(newRoom);
        return newRoom;
    }

    public Room updateRoom(Long roomId, Room updated) {
        validateRoom(updated);
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

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer addCustomer(String name, String surname, String email, String phone)
            throws InvalidCustomerDataException {
        Customer customer = new Customer(clean(name), clean(surname), clean(email), clean(phone));
        customer.setCustomerId(customerIdCounter.getAndIncrement());
        customers.add(customer);
        return customer;
    }

    public Customer updateCustomer(Long customerId, String name, String surname, String email, String phone)
            throws InvalidCustomerDataException {
        Customer customer = findCustomerById(customerId);
        if (customer == null) return null;

        String cleanedName = clean(name);
        String cleanedSurname = clean(surname);
        String cleanedEmail = clean(email);
        String cleanedPhone = clean(phone);
        validateCustomer(cleanedName, cleanedSurname, cleanedEmail, cleanedPhone);

        customer.setName(cleanedName);
        customer.setSurname(cleanedSurname);
        customer.setEmail(cleanedEmail);
        customer.setPhone(cleanedPhone);
        return customer;
    }

    public boolean deleteCustomer(Long customerId) {
        return customers.removeIf(c -> c.getCustomerId().equals(customerId));
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public Reservation createReservation(String customerName, String roomNumber, LocalDate checkIn, LocalDate checkOut)
            throws RoomNotAvailableException, InvalidCustomerDataException, InvalidReservationDataException {
        validateReservation(customerName, roomNumber, checkIn, checkOut);

        Room room = rooms.stream()
                .filter(r -> r.getRoomNumber().equals(roomNumber.trim()))
                .findFirst()
                .orElseThrow(() -> new RoomNotAvailableException("Room not found: " + roomNumber));

        if (!room.isAvailable()) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not available.");
        }

        Customer customer = findOrCreateGuest(customerName);
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

    private void addSeedRoom(Room room) {
        room.setRoomId(roomIdCounter.getAndIncrement());
        rooms.add(room);
    }

    private void validateRoom(Room room) {
        if (room.getRoomNumber() == null || room.getRoomNumber().isBlank()) {
            throw new IllegalArgumentException("Room number is required.");
        }
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Room capacity must be at least 1.");
        }
        if (room.getPricePerNight() < 0) {
            throw new IllegalArgumentException("Price per night cannot be negative.");
        }
        if (room.getType() == null || room.getType().isBlank()) {
            room.setType("Single");
        }
    }

    private void validateReservation(String customerName, String roomNumber, LocalDate checkIn, LocalDate checkOut)
            throws InvalidReservationDataException {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new InvalidReservationDataException("Guest name is required.");
        }
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new InvalidReservationDataException("Please choose an available room.");
        }
        if (checkIn == null || checkOut == null) {
            throw new InvalidReservationDataException("Check-in and check-out dates are required.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidReservationDataException("Check-out date must be after check-in date.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidReservationDataException("Check-in date cannot be in the past.");
        }
    }

    private Customer findOrCreateGuest(String customerName) throws InvalidCustomerDataException {
        String cleanedName = customerName.trim().replaceAll("\\s+", " ");
        Customer existing = customers.stream()
                .filter(c -> c.getFullName().equalsIgnoreCase(cleanedName))
                .findFirst()
                .orElse(null);
        if (existing != null) return existing;

        String[] parts = cleanedName.split(" ", 2);
        String first = parts[0];
        String last = parts.length > 1 ? parts[1] : "-";
        Customer customer = new Customer(first, last, "guest@hotel.com", "0000000000");
        customer.setCustomerId(customerIdCounter.getAndIncrement());
        customers.add(customer);
        return customer;
    }

    private Room findRoomById(Long id) {
        return rooms.stream().filter(r -> r.getRoomId().equals(id)).findFirst().orElse(null);
    }

    private Customer findCustomerById(Long id) {
        return customers.stream().filter(c -> c.getCustomerId().equals(id)).findFirst().orElse(null);
    }

    private void validateCustomer(String name, String surname, String email, String phone)
            throws InvalidCustomerDataException {
        if (name.isEmpty()) throw new InvalidCustomerDataException("Name can't be null or empty");
        if (surname.isEmpty()) throw new InvalidCustomerDataException("Surname can't be null or empty");
        if (email.isEmpty()) throw new InvalidCustomerDataException("Email can't be null or empty");
        if (!email.contains("@")) throw new InvalidCustomerDataException("Invalid email format");
        if (phone.isEmpty()) throw new InvalidCustomerDataException("Phone can't be null or empty");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }
}
