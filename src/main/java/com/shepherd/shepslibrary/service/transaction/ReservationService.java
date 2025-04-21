package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;

import java.util.UUID;

public interface ReservationService {
    ReservationResponse reserveBook(UUID bookId);
    ReservationResponse getReservationById(UUID reservationId);
    PaginatedResponse<ReservationResponse> getAllReservationByUserId(UUID userId, int pageNumber);
    PaginatedResponse<ReservationResponse> getAllReservations(int pageNumber);
    String deleteReservation(UUID reservationId, UUID userId);
    String deleteAllReservation(UUID userId);
//    void sendAvailableBooksNotification();
}
