package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;
import com.shepherd.shepslibrary.security.AuthenticatedUser;

public interface ReservationService {
    ReservationResponse reserveBook(String bookId, AuthenticatedUser authenticatedUser);
    ReservationResponse getReservationById(String reservationId);
    PaginationResponse<ReservationResponse> getAllReservationByUserId(String userId, PaginationRequest paginationRequest);
    PaginationResponse<ReservationResponse> getAllReservations(PaginationRequest paginationRequest);
    String deleteReservation(String reservationId, AuthenticatedUser authenticatedUser);
    void deleteAllReservation(String userId);
//    void sendAvailableBooksNotification();
}