package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Page<Reservation> findAllByUserId(UUID userId, Pageable pageable);
    void deleteByIdAndUserId(UUID reservationId, UUID userId);
    void deleteAllByUserId(UUID userId);
}
