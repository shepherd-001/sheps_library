package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;
import com.shepherd.shepslibrary.data.dto.response.ReserveBookResponse;

import java.util.UUID;

public interface ReservationService {
    ReserveBookResponse reserveBook(UUID bookId);
    ReservationResponse getReservationById(UUID reservationId);
    PaginatedResponse<ReservationResponse> getAllReservationByUserId(UUID userId, int pageNumber);
    PaginatedResponse<ReservationResponse> getAllReservations(int pageNumber);
    void deleteReservation(UUID reservationId, UUID userId);
    void deleteAllReservation(UUID userId);
//    void sendAvailableBooksNotification();
}
