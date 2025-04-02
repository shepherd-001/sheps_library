package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.TransactionResponse;

import java.util.UUID;

public interface TransactionService {
    BaseResponse<TransactionResponse> borrowBook(BorrowBookRequest request);
    BaseResponse<TransactionResponse> returnBook(UUID transactionId);
    BaseResponse<PaginatedResponse<TransactionResponse>> getAllTransactionByUserId(UUID userId, int pageNumber);
    BaseResponse<PaginatedResponse<TransactionResponse>> getAllTransactions(int pageNumber);
//    void sendBookOverdueNotifications();
}
