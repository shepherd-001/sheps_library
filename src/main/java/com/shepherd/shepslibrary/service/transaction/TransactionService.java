package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.response.TransactionResponse;
import com.shepherd.shepslibrary.security.AuthenticatedUser;

import java.util.UUID;

public interface TransactionService {
    TransactionResponse borrowBook(BorrowBookRequest request, AuthenticatedUser authenticatedUser);
    TransactionResponse returnBook(UUID transactionId);
    PaginationResponse<TransactionResponse> getAllTransactionByUserId(UUID userId, PaginationRequest paginationRequest);
    PaginationResponse<TransactionResponse> getAllTransactions(PaginationRequest paginationRequest);
//    void sendBookOverdueNotifications();
}
