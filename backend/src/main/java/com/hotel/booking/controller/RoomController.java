package com.hotel.booking.controller;

import com.hotel.booking.model.Room;
import com.hotel.booking.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final BookingService bookingService;

    public RoomController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return bookingService.getAllRooms();
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bookingService.addRoom(toRoom(body)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Room updated = bookingService.updateRoom(id, toRoom(body));
        if (updated == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Room not found."));
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        if (!bookingService.deleteRoom(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Room not found."));
        }
        return ResponseEntity.noContent().build();
    }

    private Room toRoom(Map<String, Object> body) {
        Room room = new Room();
        room.setRoomNumber(readString(body, "roomNumber"));
        room.setType(readString(body, "type"));
        room.setCapacity(readInt(body, "capacity"));
        room.setStatus(readString(body, "status"));
        return room;
    }

    private String readString(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? "" : value.toString().trim();
    }

    private int readInt(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(value.toString());
    }

}
