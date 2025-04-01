package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.TransactionResponse;

public interface TransactionService {
    BaseResponse<TransactionResponse> borrowBook(BorrowBookRequest request);
    BaseResponse<TransactionResponse> returnBook(String transactionId);
    BaseResponse<PaginatedResponse<TransactionResponse>> getAllTransactionByUserId(String userId, int pageNumber);
    BaseResponse<PaginatedResponse<TransactionResponse>> getAllTransactions(int pageNumber);
//    void sendBookOverdueNotifications();
}
