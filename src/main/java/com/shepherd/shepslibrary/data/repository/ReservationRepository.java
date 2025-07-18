package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    Page<Reservation> findAllByUserId(String userId, Pageable pageable);
//    void deleteByIdAndUserId(String reservationId, String userId);
    @Query("""
           select reservation from Reservation reservation
           where reservation.book.isAvailable = true
           """)
    Page<Reservation> findAllAvailableReservations(Pageable pageable);
    boolean existsByUserIdAndBookId(String userId, String bookId);
    boolean existsById(String reservationId);
    boolean existsByIdAndUserId(String reservationId, String userId);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.id = :reservationId AND r.user.id = :userId")
    int deleteByReservationIdAndUserId(@Param("reservationId") String reservationId,
                            @Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.user.id = :userId")
    void deleteAllByUserId(@Param("userId") String userId);

}
