package com.hotel.booking.repository;

import com.hotel.booking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    Optional<Reservation> findTopByOrderByReservationIdDesc();
}
