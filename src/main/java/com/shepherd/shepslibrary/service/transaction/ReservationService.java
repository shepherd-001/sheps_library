package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;

public interface ReservationService {
    ReservationResponse reserveBook(String bookId);
    ReservationResponse getReservationById(String reservationId);
    PaginatedResponse<ReservationResponse> getAllReservationByUserId(String userId, int pageNumber);
    PaginatedResponse<ReservationResponse> getAllReservations(int pageNumber);
    String deleteReservation(String reservationId, String userId);
    String deleteAllReservation(String userId);
//    void sendAvailableBooksNotification();
}
