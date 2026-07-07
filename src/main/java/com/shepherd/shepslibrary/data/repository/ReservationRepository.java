package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Reservation;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Page<Reservation> findAllByUserId(UUID userId, Pageable pageable);
    @Query("""
           select reservation from Reservation reservation
           where reservation.book.available = true
           """)
    Page<Reservation> findAllAvailableReservations(Pageable pageable);
    boolean existsByUserIdAndBookId(UUID userId, UUID bookId);
    boolean existsById(@Nonnull UUID reservationId);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.id = :reservationId AND r.user.id = :userId")
    int deleteByReservationIdAndUserId(@Param("reservationId") UUID reservationId,
                            @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.user.id = :userId")
    int deleteAllByUserIdReturningCount(@Param("userId") UUID userId);
}
