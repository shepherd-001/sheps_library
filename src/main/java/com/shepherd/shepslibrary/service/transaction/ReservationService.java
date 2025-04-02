package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;

import java.util.UUID;

public interface ReservationService {
    BaseResponse<ReservationResponse> reserveBook(UUID bookId);
    BaseResponse<ReservationResponse> getReservationById(UUID reservationId);
    BaseResponse<PaginatedResponse<ReservationResponse>> getAllReservationByUserId(UUID userId, int pageNumber);
    BaseResponse<PaginatedResponse<ReservationResponse>> getAllReservations(int pageNumber);
    BaseResponse<String> deleteReservation(UUID reservationId, UUID userId);
    BaseResponse<String> deleteAllReservation(UUID userId);
//    void sendAvailableBooksNotification();
}
