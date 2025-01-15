package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.TransactionResponse;

import java.util.UUID;

public interface TransactionService {
    TransactionResponse borrowBook(BorrowBookRequest request);
    TransactionResponse returnBook(UUID transactionId);
    PaginatedResponse<TransactionResponse> getAllTransactionByUserId(UUID userId, int pageNumber);
    PaginatedResponse<TransactionResponse> getAllTransactions(int pageNumber);
}
