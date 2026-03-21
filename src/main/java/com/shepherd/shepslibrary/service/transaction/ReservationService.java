package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;

public interface ReservationService {
    ReservationResponse reserveBook(String bookId);
    ReservationResponse getReservationById(String reservationId);
    PaginationResponse<ReservationResponse> getAllReservationByUserId(String userId, PaginationRequest paginationRequest);
    PaginationResponse<ReservationResponse> getAllReservations(PaginationRequest paginationRequest);
    String deleteReservation(String reservationId);
    void deleteAllReservation(String userId);
//    void sendAvailableBooksNotification();
}