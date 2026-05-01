package com.hotel.booking.service;

import com.hotel.booking.exception.InvalidReservationDataException;
import com.hotel.booking.exception.RoomNotAvailableException;
import com.hotel.booking.model.Reservation;
import com.hotel.booking.model.Room;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    private BookingService newService(AuditLogService auditLogService) {
        return new BookingService(auditLogService);
    }

    @Test
    void availabilityReturnsRoomsWithoutDateConflicts() throws Exception {
        BookingService service = newService(new AuditLogService());
        service.createReservation(
                "Emily Carter",
                "101",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );

        Map<String, Object> availability = service.getAvailability(
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(4)
        );

        @SuppressWarnings("unchecked")
        List<Room> availableRooms = (List<Room>) availability.get("availableRooms");
        assertTrue(availableRooms.stream().noneMatch(room -> room.getRoomNumber().equals("101")));
        assertEquals(1L, availability.get("nights"));
    }

    @Test
    void rejectsOverlappingReservationForSameRoom() throws Exception {
        BookingService service = newService(new AuditLogService());
        service.createReservation(
                "Emily Carter",
                "101",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );

        RoomNotAvailableException error = assertThrows(RoomNotAvailableException.class, () ->
                service.createReservation(
                        "Mina Stone",
                        "101",
                        LocalDate.now().plusDays(4),
                        LocalDate.now().plusDays(6)
                )
        );
        assertEquals("Room 101 is already booked for the selected dates.", error.getMessage());
    }

    @Test
    void allowsBackToBackReservationsForSameRoom() throws Exception {
        BookingService service = newService(new AuditLogService());
        service.createReservation(
                "Emily Carter",
                "101",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );

        Reservation second = service.createReservation(
                "Mina Stone",
                "101",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7)
        );

        assertEquals("Active", second.getStatus());
    }

    @Test
    void cancellationUpdatesRoomStatusWhenNoFutureReservationBlocksIt() throws Exception {
        BookingService service = newService(new AuditLogService());
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

    @Test
    void dashboardSummaryReturnsExpectedCountsAndRevenue() throws Exception {
        BookingService service = newService(new AuditLogService());
        service.createReservation(
                "Emily Carter",
                "101",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        Map<String, Object> summary = service.getDashboardSummary();

        assertEquals(5L, summary.get("totalRooms"));
        assertEquals(1, summary.get("activeReservations"));
        assertEquals(1L, summary.get("bookedRooms"));
        assertEquals(200.0, (double) summary.get("estimatedRevenue"));
    }

    @Test
    void auditLogRecordsReservationActions() throws Exception {
        AuditLogService auditLogService = new AuditLogService();
        BookingService service = newService(auditLogService);
        Reservation reservation = service.createReservation(
                "Emily Carter",
                "103",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
        service.cancelReservation(reservation.getReservationId());

        assertTrue(auditLogService.getAll().stream()
                .anyMatch(entry -> entry.getAction().equals("RESERVATION_CREATED")));
        assertTrue(auditLogService.getAll().stream()
                .anyMatch(entry -> entry.getAction().equals("RESERVATION_CANCELLED")));
    }

    @Test
    void rejectsInvalidReservationDates() {
        BookingService service = newService(new AuditLogService());

        assertThrows(InvalidReservationDataException.class, () ->
                service.createReservation(
                        "Emily Carter",
                        "101",
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(1)
                )
        );
    }
}
