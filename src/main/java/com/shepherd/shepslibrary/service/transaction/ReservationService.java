package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;
import com.shepherd.shepslibrary.security.AuthenticatedUser;

import java.util.UUID;

public interface ReservationService {
    ReservationResponse reserveBook(UUID bookId, AuthenticatedUser authenticatedUser);
    ReservationResponse getReservationById(UUID reservationId);
    PaginationResponse<ReservationResponse> getAllReservationByUserId(UUID userId, PaginationRequest paginationRequest);
    PaginationResponse<ReservationResponse> getAllReservations(PaginationRequest paginationRequest);
    String deleteReservation(UUID reservationId, AuthenticatedUser authenticatedUser);
    void deleteAllReservation(UUID userId);
//    void sendAvailableBooksNotification();
}