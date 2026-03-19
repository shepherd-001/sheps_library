package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse borrowBook(BorrowBookRequest request);
    TransactionResponse returnBook(String transactionId);
    PaginationResponse<TransactionResponse> getAllTransactionByUserId(String userId, PaginationRequest paginationRequest);
    PaginationResponse<TransactionResponse> getAllTransactions(PaginationRequest paginationRequest);
//    void sendBookOverdueNotifications();
}
