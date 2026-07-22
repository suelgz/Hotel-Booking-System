package com.hotel.booking.controller;

import com.hotel.booking.dto.RoomRequest;
import com.hotel.booking.exception.InvalidRoomDataException;
import com.hotel.booking.model.Room;
import com.hotel.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Room> createRoom(@Valid @RequestBody RoomRequest request) throws InvalidRoomDataException {
        return ResponseEntity.ok(bookingService.addRoom(toRoom(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request)
            throws InvalidRoomDataException {
        Room updated = bookingService.updateRoom(id, toRoom(request));
        if (updated == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Room not found."));
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) throws InvalidRoomDataException {
        if (!bookingService.deleteRoom(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Room not found."));
        }
        return ResponseEntity.noContent().build();
    }

    private Room toRoom(RoomRequest request) {
        Room room = new Room();
        room.setRoomNumber(request.roomNumber());
        room.setType(request.type());
        room.setCapacity(request.capacity());
        if (request.pricePerNight() != null) {
            room.setPrice(request.pricePerNight());
        }
        room.setStatus(request.status());
        return room;
    }
}
