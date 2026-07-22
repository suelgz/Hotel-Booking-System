package com.hotel.booking.controller;

import com.hotel.booking.dto.CustomerRequest;
import com.hotel.booking.exception.InvalidCustomerDataException;
import com.hotel.booking.model.Customer;
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
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerRequest request)
            throws InvalidCustomerDataException {
        String[] nameParts = splitFullName(request.fullName());
        Customer created = bookingService.addCustomer(
                nameParts[0],
                nameParts[1],
                request.email(),
                request.phone()
        );
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request)
            throws InvalidCustomerDataException {
        String[] nameParts = splitFullName(request.fullName());
        Customer updated = bookingService.updateCustomer(
                id,
                nameParts[0],
                nameParts[1],
                request.email(),
                request.phone()
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
