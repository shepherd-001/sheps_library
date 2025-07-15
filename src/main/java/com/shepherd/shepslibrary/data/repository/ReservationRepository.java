package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    Page<Reservation> findAllByUserId(String userId, Pageable pageable);
    void deleteByIdAndUserId(String reservationId, String userId);
    void deleteAllByUserId(String userId);
    @Query("""
           select reservation from Reservation reservation
           where reservation.book.isAvailable = true
           """)
    Page<Reservation> findAllAvailableReservations(Pageable pageable);
    boolean existsByUserIdAndBookId(String userId, String bookId);
    boolean existsByIdAndUserId(String reservationId, String userId);
}
