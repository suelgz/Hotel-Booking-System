package com.hotel.booking.controller;

import com.hotel.booking.model.Room;
import com.hotel.booking.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final BookingService bookingService;

    public RoomController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // GET /api/rooms
    @GetMapping
    public List<Room> getAllRooms() {
        return bookingService.getAllRooms();
    }

    // POST /api/rooms
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        return ResponseEntity.ok(bookingService.addRoom(room));
    }

    // PUT /api/rooms/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        Room updated = bookingService.updateRoom(id, room);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/rooms/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (!bookingService.deleteRoom(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
