package com.hotel.booking.service;

import com.hotel.booking.exception.InvalidCustomerDataException;
import com.hotel.booking.exception.InvalidReservationDataException;
import com.hotel.booking.exception.RoomNotAvailableException;
import com.hotel.booking.model.Reservation;
import com.hotel.booking.model.Room;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    @Test
    void createsReservationAndMarksRoomOccupied() throws Exception {
        BookingService service = new BookingService();

        Reservation reservation = service.createReservation(
                "SingleName",
                "101",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        Room room = service.getAllRooms().stream()
                .filter(candidate -> candidate.getRoomNumber().equals("101"))
                .findFirst()
                .orElseThrow();

        assertEquals("Active", reservation.getStatus());
        assertEquals("Occupied", room.getStatus());
        assertEquals("-", reservation.getCustomer().getSurname());
    }

    @Test
    void rejectsInvalidReservationDates() {
        BookingService service = new BookingService();

        assertThrows(InvalidReservationDataException.class, () ->
                service.createReservation(
                        "Emily Carter",
                        "101",
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(1)
                )
        );
    }

    @Test
    void cancellingReservationMakesRoomAvailable()
            throws InvalidCustomerDataException, RoomNotAvailableException, InvalidReservationDataException {
        BookingService service = new BookingService();
        Reservation reservation = service.createReservation(
                "Emily Carter",
                "102",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );

        assertTrue(service.cancelReservation(reservation.getReservationId()));

        Room room = service.getAllRooms().stream()
                .filter(candidate -> candidate.getRoomNumber().equals("102"))
                .findFirst()
                .orElseThrow();
        assertEquals("Cancelled", reservation.getStatus());
        assertEquals("Available", room.getStatus());
    }
}
