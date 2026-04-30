package com.hotel.booking.controller;

import com.hotel.booking.exception.InvalidCustomerDataException;
import com.hotel.booking.exception.InvalidReservationDataException;
import com.hotel.booking.exception.RoomNotAvailableException;
import com.hotel.booking.model.Reservation;
import com.hotel.booking.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final BookingService bookingService;

    public ReservationController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return bookingService.getAllReservations();
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Map<String, String> body)
            throws RoomNotAvailableException, InvalidCustomerDataException, InvalidReservationDataException {
        Reservation created = bookingService.createReservation(
                body.get("customerName"),
                body.get("roomNumber"),
                LocalDate.parse(body.getOrDefault("checkInDate", "")),
                LocalDate.parse(body.getOrDefault("checkOutDate", ""))
        );
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable String id) {
        boolean success = bookingService.cancelReservation(id);
        if (!success) {
            return ResponseEntity.status(404).body(Map.of("error", "Reservation not found."));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
