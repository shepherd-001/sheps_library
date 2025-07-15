package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse borrowBook(BorrowBookRequest request);
    TransactionResponse returnBook(String transactionId);
    PaginatedResponse<TransactionResponse> getAllTransactionByUserId(String userId, int pageNumber);
    PaginatedResponse<TransactionResponse> getAllTransactions(int pageNumber);
//    void sendBookOverdueNotifications();
}
