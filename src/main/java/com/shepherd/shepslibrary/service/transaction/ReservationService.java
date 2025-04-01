package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;

public interface ReservationService {
    BaseResponse<ReservationResponse> reserveBook(String bookId);
    BaseResponse<ReservationResponse> getReservationById(String reservationId);
    BaseResponse<PaginatedResponse<ReservationResponse>> getAllReservationByUserId(String userId, int pageNumber);
    BaseResponse<PaginatedResponse<ReservationResponse>> getAllReservations(int pageNumber);
    BaseResponse<String> deleteReservation(String reservationId, String userId);
    BaseResponse<String> deleteAllReservation(String userId);
//    void sendAvailableBooksNotification();
}
