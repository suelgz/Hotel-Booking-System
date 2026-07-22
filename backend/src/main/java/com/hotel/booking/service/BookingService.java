package com.hotel.booking.service;

import com.hotel.booking.exception.InvalidCustomerDataException;
import com.hotel.booking.exception.InvalidReservationDataException;
import com.hotel.booking.exception.InvalidRoomDataException;
import com.hotel.booking.exception.RoomNotAvailableException;
import com.hotel.booking.model.Customer;
import com.hotel.booking.model.Reservation;
import com.hotel.booking.model.ReservationStatus;
import com.hotel.booking.model.Room;
import com.hotel.booking.model.RoomStatus;
import com.hotel.booking.model.RoomType;
import com.hotel.booking.model.StandardRoom;
import com.hotel.booking.model.SuiteRoom;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookingService {

    private final List<Room> rooms = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final AuditLogService auditLogService;

    private final AtomicLong roomIdCounter = new AtomicLong(1);
    private final AtomicLong customerIdCounter = new AtomicLong(1);
    private int reservationCounter = 1;

    public BookingService(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;

        addSeedRoom(new StandardRoom("101", 2, RoomType.SINGLE.getPricePerNight(), true));
        addSeedRoom(new StandardRoom("102", 2, RoomType.SINGLE.getPricePerNight(), true));
        addSeedRoom(new StandardRoom("103", 4, RoomType.DOUBLE.getPricePerNight(), true));
        addSeedRoom(new SuiteRoom("201", 2, RoomType.SUITE.getPricePerNight(), true, 50, "Deluxe", true));
        addSeedRoom(new SuiteRoom("202", 6, RoomType.SUITE.getPricePerNight(), true, 75, "Presidential", true));
    }

    public List<Room> getAllRooms() {
        refreshAllRoomStatuses();
        return new ArrayList<>(rooms);
    }

    public Room addRoom(Room newRoom) throws InvalidRoomDataException {
        validateRoom(newRoom, null);
        newRoom.setRoomId(roomIdCounter.getAndIncrement());
        rooms.add(newRoom);
        refreshRoomStatus(newRoom);
        auditLogService.record(
                "ROOM_CREATED",
                "Room " + newRoom.getRoomNumber() + " created",
                "room"
        );
        return newRoom;
    }

    public Room updateRoom(Long roomId, Room updated) throws InvalidRoomDataException {
        Room room = findRoomById(roomId);
        if (room == null) return null;

        validateRoom(updated, roomId);
        room.setRoomNumber(updated.getRoomNumber());
        room.setRoomType(updated.getRoomType());
        room.setCapacity(updated.getCapacity());
        room.setPrice(updated.getRoomType().getPricePerNight());
        room.setRoomStatus(updated.getRoomStatus());
        refreshRoomStatus(room);
        auditLogService.record(
                "ROOM_UPDATED",
                "Room " + room.getRoomNumber() + " updated",
                "room"
        );
        return room;
    }

    public boolean deleteRoom(Long roomId) throws InvalidRoomDataException {
        Room room = findRoomById(roomId);
        if (room == null) return false;
        if (hasAnyActiveReservation(room)) {
            throw new InvalidRoomDataException("Room has active or upcoming reservations and cannot be deleted.");
        }
        rooms.remove(room);
        auditLogService.record(
                "ROOM_DELETED",
                "Room " + room.getRoomNumber() + " deleted",
                "room"
        );
        return true;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public Customer addCustomer(String name, String surname, String email, String phone)
            throws InvalidCustomerDataException {
        String cleanedName = clean(name);
        String cleanedSurname = clean(surname);
        String cleanedEmail = clean(email);
        String cleanedPhone = clean(phone);
        validateCustomer(cleanedName, cleanedSurname, cleanedEmail, cleanedPhone);

        Customer customer = new Customer(cleanedName, cleanedSurname, cleanedEmail, cleanedPhone);
        customer.setCustomerId(customerIdCounter.getAndIncrement());
        customers.add(customer);
        auditLogService.record(
                "CUSTOMER_CREATED",
                "Customer " + customer.getFullName() + " created",
                "customer"
        );
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
        auditLogService.record(
                "CUSTOMER_UPDATED",
                "Customer " + customer.getFullName() + " updated",
                "customer"
        );
        return customer;
    }

    public boolean deleteCustomer(Long customerId) {
        Customer customer = findCustomerById(customerId);
        if (customer == null) return false;
        customers.remove(customer);
        auditLogService.record(
                "CUSTOMER_DELETED",
                "Customer " + customer.getFullName() + " deleted",
                "customer"
        );
        return true;
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public Reservation createReservation(String customerName, String roomNumber, LocalDate checkIn, LocalDate checkOut)
            throws RoomNotAvailableException, InvalidCustomerDataException, InvalidReservationDataException {
        validateReservation(customerName, roomNumber, checkIn, checkOut);

        Room room = rooms.stream()
                .filter(r -> r.getRoomNumber().equals(clean(roomNumber)))
                .findFirst()
                .orElseThrow(() -> new RoomNotAvailableException("Room not found: " + roomNumber));

        if (room.getRoomStatus().blocksBooking()) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not ready for booking.");
        }
        if (hasOverlappingActiveReservation(room, checkIn, checkOut)) {
            throw new RoomNotAvailableException(
                    "Room " + room.getRoomNumber() + " is already booked for the selected dates."
            );
        }

        Customer customer = findOrCreateGuest(customerName);
        String resId = String.format("RES-%03d", reservationCounter++);
        Reservation reservation = new Reservation(resId, customer, room, checkIn, checkOut);

        reservation.book();
        reservations.add(reservation);
        refreshRoomStatus(room);
        auditLogService.record(
                "RESERVATION_CREATED",
                "Reservation " + resId + " created for Room " + room.getRoomNumber(),
                "reservation"
        );
        return reservation;
    }

    public boolean cancelReservation(String reservationId) {
        Reservation reservation = reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst()
                .orElse(null);
        if (reservation == null) return false;

        reservation.cancel();
        refreshRoomStatus(reservation.getRoom());
        auditLogService.record(
                "RESERVATION_CANCELLED",
                "Reservation " + reservationId + " cancelled for Room " + reservation.getRoomNumber(),
                "reservation"
        );
        return true;
    }

    public Map<String, Object> getAvailability(LocalDate checkIn, LocalDate checkOut)
            throws InvalidReservationDataException {
        validateStayDates(checkIn, checkOut);
        refreshAllRoomStatuses();
        List<Room> availableRooms = rooms.stream()
                .filter(room -> isRoomAvailableForDates(room, checkIn, checkOut))
                .toList();

        return Map.of(
                "checkInDate", checkIn,
                "checkOutDate", checkOut,
                "nights", ChronoUnit.DAYS.between(checkIn, checkOut),
                "availableRooms", availableRooms
        );
    }

    public Map<String, Object> getDashboardSummary() {
        refreshAllRoomStatuses();
        LocalDate today = LocalDate.now();
        long totalRooms = rooms.size();
        long availableRooms = rooms.stream().filter(room -> room.getRoomStatus() == RoomStatus.AVAILABLE).count();
        long bookedRooms = rooms.stream().filter(room -> room.getRoomStatus() == RoomStatus.BOOKED).count();
        long occupiedRooms = rooms.stream().filter(room -> room.getRoomStatus() == RoomStatus.OCCUPIED).count();
        long maintenanceRooms = rooms.stream().filter(room -> room.getRoomStatus() == RoomStatus.MAINTENANCE).count();
        List<Reservation> activeReservations = getActiveReservations();
        long cancelledReservations = reservations.stream()
                .filter(Reservation::isCancelled)
                .count();
        int occupancyRate = totalRooms == 0
                ? 0
                : (int) Math.round(((double) occupiedRooms / totalRooms) * 100);
        double estimatedRevenue = activeReservations.stream()
                .mapToDouble(Reservation::getTotalPrice)
                .sum();

        List<Reservation> todayArrivals = activeReservations.stream()
                .filter(reservation -> today.equals(reservation.getCheckInDate()))
                .toList();
        List<Reservation> todayDepartures = activeReservations.stream()
                .filter(reservation -> today.equals(reservation.getCheckOutDate()))
                .toList();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalRooms", totalRooms);
        summary.put("availableRooms", availableRooms);
        summary.put("bookedRooms", bookedRooms);
        summary.put("occupiedRooms", occupiedRooms);
        summary.put("maintenanceRooms", maintenanceRooms);
        summary.put("activeReservations", activeReservations.size());
        summary.put("cancelledReservations", cancelledReservations);
        summary.put("occupancyRate", occupancyRate);
        summary.put("estimatedRevenue", estimatedRevenue);
        summary.put("todayArrivals", todayArrivals);
        summary.put("todayDepartures", todayDepartures);
        summary.put("recentAuditLogs", auditLogService.getRecent(8));
        return summary;
    }

    private boolean isRoomAvailableForDates(Room room, LocalDate checkIn, LocalDate checkOut) {
        return !room.getRoomStatus().blocksBooking() && !hasOverlappingActiveReservation(room, checkIn, checkOut);
    }

    private boolean hasOverlappingActiveReservation(Room room, LocalDate checkIn, LocalDate checkOut) {
        return getActiveReservations().stream()
                .filter(reservation -> reservation.getRoomId().equals(room.getRoomId()))
                .anyMatch(reservation -> reservation.overlaps(checkIn, checkOut));
    }

    private boolean hasAnyActiveReservation(Room room) {
        return getActiveReservations().stream()
                .anyMatch(reservation -> reservation.getRoomId().equals(room.getRoomId()));
    }

    private List<Reservation> getActiveReservations() {
        return reservations.stream()
                .filter(reservation -> reservation.getReservationStatus() == ReservationStatus.ACTIVE)
                .toList();
    }

    private void refreshAllRoomStatuses() {
        rooms.forEach(this::refreshRoomStatus);
    }

    private void refreshRoomStatus(Room room) {
        if (room.getRoomStatus().blocksBooking()) {
            return;
        }

        LocalDate today = LocalDate.now();
        boolean isOccupied = getActiveReservations().stream()
                .anyMatch(reservation -> reservation.getRoomId().equals(room.getRoomId())
                        && reservation.isCurrentStay(today));
        boolean hasUpcoming = getActiveReservations().stream()
                .anyMatch(reservation -> reservation.getRoomId().equals(room.getRoomId())
                        && reservation.isUpcoming(today));

        if (isOccupied) {
            room.setRoomStatus(RoomStatus.OCCUPIED);
        } else if (hasUpcoming) {
            room.setRoomStatus(RoomStatus.BOOKED);
        } else {
            room.setRoomStatus(RoomStatus.AVAILABLE);
        }
    }

    private void addSeedRoom(Room room) {
        room.setRoomId(roomIdCounter.getAndIncrement());
        room.setPrice(room.getRoomType().getPricePerNight());
        rooms.add(room);
    }

    private void validateRoom(Room room, Long existingRoomId) throws InvalidRoomDataException {
        String roomNumber = clean(room.getRoomNumber());
        if (roomNumber.isEmpty()) {
            throw new InvalidRoomDataException("Room number is required.");
        }
        if (room.getCapacity() <= 0) {
            throw new InvalidRoomDataException("Room capacity must be at least 1.");
        }
        boolean duplicate = rooms.stream()
                .anyMatch(existing -> existing.getRoomNumber().equalsIgnoreCase(roomNumber)
                        && (existingRoomId == null || !existing.getRoomId().equals(existingRoomId)));
        if (duplicate) {
            throw new InvalidRoomDataException("Room number already exists.");
        }

        try {
            RoomType roomType = RoomType.fromDisplayName(room.getType());
            RoomStatus roomStatus = RoomStatus.fromDisplayName(room.getStatus());
            room.setRoomNumber(roomNumber);
            room.setRoomType(roomType);
            room.setRoomStatus(roomStatus);
            room.setPrice(roomType.getPricePerNight());
        } catch (IllegalArgumentException ex) {
            throw new InvalidRoomDataException(ex.getMessage());
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
        validateStayDates(checkIn, checkOut);
    }

    private void validateStayDates(LocalDate checkIn, LocalDate checkOut)
            throws InvalidReservationDataException {
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
        String cleanedName = clean(customerName);
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
        auditLogService.record(
                "CUSTOMER_CREATED",
                "Customer " + customer.getFullName() + " created",
                "customer"
        );
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
