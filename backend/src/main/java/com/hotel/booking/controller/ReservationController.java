package com.hotel.booking.controller;

import com.hotel.booking.exception.InvalidCustomerDataException;
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

    // GET /api/reservations
    @GetMapping
    public List<Reservation> getAllReservations() {
        return bookingService.getAllReservations();
    }

    // POST /api/reservations
    // Body: { "customerName": "Emily Carter", "roomNumber": "101",
    //         "checkInDate": "2025-05-01", "checkOutDate": "2025-05-04" }
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Map<String, String> body) {
        try {
            Reservation created = bookingService.createReservation(
                    body.get("customerName"),
                    body.get("roomNumber"),
                    LocalDate.parse(body.get("checkInDate")),
                    LocalDate.parse(body.get("checkOutDate"))
            );
            return ResponseEntity.ok(created);
        } catch (RoomNotAvailableException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (InvalidCustomerDataException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PATCH /api/reservations/{id}/cancel
    // Senin orijinal Reservation.cancel() metodunu çağırıyor
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable String id) {
        boolean success = bookingService.cancelReservation(id);
        if (!success) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("success", true));
    }
}
