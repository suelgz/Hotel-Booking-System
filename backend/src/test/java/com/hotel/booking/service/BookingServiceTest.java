package com.hotel.booking.service;

import com.hotel.booking.exception.InvalidRoomDataException;
import com.hotel.booking.exception.RoomNotAvailableException;
import com.hotel.booking.model.AuditLogEntry;
import com.hotel.booking.model.Customer;
import com.hotel.booking.model.Reservation;
import com.hotel.booking.model.Room;
import com.hotel.booking.model.RoomStatus;
import com.hotel.booking.model.RoomType;
import com.hotel.booking.repository.CustomerRepository;
import com.hotel.booking.repository.ReservationRepository;
import com.hotel.booking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AuditLogService auditLogService;

    private BookingService service;

    @BeforeEach
    void setUp() {
        service = new BookingService(roomRepository, customerRepository, reservationRepository, auditLogService);
        lenient().when(auditLogService.record(anyString(), anyString(), anyString()))
                .thenReturn(new AuditLogEntry(1L, LocalDateTime.now(), "ACTION", "message", "type"));
    }

    @Test
    void fixedRoomPricingComesFromRoomType() throws Exception {
        Room room = new Room();
        room.setRoomNumber("301");
        room.setType("Double");
        room.setCapacity(3);
        room.setPricePerNight(999.0);
        room.setStatus("Available");

        when(roomRepository.findByRoomNumberIgnoreCase("301")).thenReturn(Optional.empty());
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.findAll()).thenReturn(List.of());

        Room created = service.addRoom(room);

        assertEquals(120.0, created.getPricePerNight());
    }

    @Test
    void rejectsDuplicateRoomNumbers() {
        Room existing = new Room();
        existing.setRoomId(1L);
        existing.setRoomNumber("101");

        Room room = new Room();
        room.setRoomNumber("101");
        room.setType("Single");
        room.setCapacity(2);
        room.setStatus("Available");

        when(roomRepository.findByRoomNumberIgnoreCase("101")).thenReturn(Optional.of(existing));

        InvalidRoomDataException error = assertThrows(InvalidRoomDataException.class, () -> service.addRoom(room));
        assertEquals("Room number already exists.", error.getMessage());
    }

    @Test
    void createsReservationWithNextGeneratedId() throws Exception {
        Room room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.SINGLE);
        room.setRoomStatus(RoomStatus.AVAILABLE);
        room.setCapacity(2);
        room.setPrice(100.0);

        when(roomRepository.findByRoomNumberIgnoreCase("101")).thenReturn(Optional.of(room));
        when(customerRepository.findAll()).thenReturn(List.of());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setCustomerId(1L);
            return customer;
        });
        when(reservationRepository.findAll()).thenReturn(List.of());
        when(reservationRepository.existsById("RES-001")).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation reservation = service.createReservation(
                "Emily Carter",
                "101",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        assertEquals("RES-001", reservation.getReservationId());
        assertEquals(200.0, reservation.getTotalPrice());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void rejectsOverlappingReservationForSameRoom() throws Exception {
        Room room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        room.setRoomType(RoomType.SINGLE);
        room.setRoomStatus(RoomStatus.AVAILABLE);
        room.setCapacity(2);
        room.setPrice(100.0);

        Customer customer = new Customer("Emily", "Carter", "emily@example.com", "1234567890");
        customer.setCustomerId(1L);
        Reservation existing = new Reservation(
                "RES-001",
                customer,
                room,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5)
        );
        existing.book();

        when(roomRepository.findByRoomNumberIgnoreCase("101")).thenReturn(Optional.of(room));
        when(reservationRepository.findAll()).thenReturn(List.of(existing));

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
}
