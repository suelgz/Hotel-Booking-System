package com.hotel.booking.controller;

import com.hotel.booking.exception.InvalidCustomerDataException;
import com.hotel.booking.model.Customer;
import com.hotel.booking.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final BookingService bookingService;

    public CustomerController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // GET /api/customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return bookingService.getAllCustomers();
    }

    // POST /api/customers
    // Body: { "fullName": "Emily Carter", "email": "...", "phone": "..." }
    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Map<String, String> body) {
        try {
            // fullName'i name + surname olarak parçala
            String fullName = body.getOrDefault("fullName", "");
            String[] parts = fullName.trim().split(" ", 2);
            String name = parts[0];
            String surname = parts.length > 1 ? parts[1] : "";

            Customer created = bookingService.addCustomer(
                    name, surname,
                    body.get("email"),
                    body.get("phone")
            );
            return ResponseEntity.ok(created);
        } catch (InvalidCustomerDataException e) {
            // Senin orijinal hata mesajın frontend'e gönderiliyor
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/customers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id,
                                            @RequestBody Map<String, String> body) {
        String fullName = body.getOrDefault("fullName", "");
        String[] parts = fullName.trim().split(" ", 2);
        String name = parts[0];
        String surname = parts.length > 1 ? parts[1] : "";

        Customer updated = bookingService.updateCustomer(id, name, surname,
                body.get("email"), body.get("phone"));
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/customers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (!bookingService.deleteCustomer(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
