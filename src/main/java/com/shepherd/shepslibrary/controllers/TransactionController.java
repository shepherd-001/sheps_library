package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.service.transaction.TransactionService;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/borrow-book")
    public ResponseEntity<ApiResponse<?>> borrowBook(@Valid @RequestBody BorrowBookRequest borrowBookRequest){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(transactionService.borrowBook(borrowBookRequest)));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/return-book")
    public ResponseEntity<ApiResponse<?>> returnBook(@RequestParam @NotBlank(message = ValidationMessage.NULL_TRANSACTION_ID)
                                                         String transactionId){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(transactionService.returnBook(transactionId)));
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<ApiResponse<?>> getAllTransactions(@PathVariable @NotBlank(message = ValidationMessage.NULL_USER_ID)
                                                                 String userId,
                                                     @RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(transactionService.getAllTransactionByUserId(userId, pageNumber)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<?>> getAllTransactions(@RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(transactionService.getAllTransactions(pageNumber)));
    }
}
