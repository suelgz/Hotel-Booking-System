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

    @GetMapping
    public List<Customer> getAllCustomers() {
        return bookingService.getAllCustomers();
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Map<String, String> body)
            throws InvalidCustomerDataException {
        String[] nameParts = splitFullName(body.get("fullName"));
        Customer created = bookingService.addCustomer(
                nameParts[0],
                nameParts[1],
                body.get("email"),
                body.get("phone")
        );
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Map<String, String> body)
            throws InvalidCustomerDataException {
        String[] nameParts = splitFullName(body.get("fullName"));
        Customer updated = bookingService.updateCustomer(
                id,
                nameParts[0],
                nameParts[1],
                body.get("email"),
                body.get("phone")
        );
        if (updated == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Customer not found."));
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        if (!bookingService.deleteCustomer(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Customer not found."));
        }
        return ResponseEntity.noContent().build();
    }

    private String[] splitFullName(String fullName) {
        String cleaned = fullName == null ? "" : fullName.trim().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            return new String[]{"", "-"};
        }
        String[] parts = cleaned.split(" ", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : "-"};
    }
}
