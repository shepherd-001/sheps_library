package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
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

    @PostMapping("/borrow-book")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAuthority('member.create')")
    public ResponseEntity<ApiResponse<?>> borrowBook(@Valid @RequestBody BorrowBookRequest borrowBookRequest){
        return ResponseEntity.ok(ApiResponse
                .success(transactionService.borrowBook(borrowBookRequest)));
    }

    @PutMapping("/return-book")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAuthority('member.update')")
    public ResponseEntity<ApiResponse<?>> returnBook(@RequestParam @NotBlank(message = ValidationMessage.NULL_TRANSACTION_ID)
                                                         String transactionId){
        return ResponseEntity.ok(ApiResponse
                .success(transactionService.returnBook(transactionId)));
    }

    @GetMapping("/all/{userId}")
    @PreAuthorize("hasAnyAuthority('member.read', 'librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllTransactions(@PathVariable @NotBlank(message = ValidationMessage.NULL_USER_ID)
                                                                 String userId, int pageNumber){
        return ResponseEntity.ok(ApiResponse
                .success(transactionService.getAllTransactionByUserId(userId, pageNumber)));
    }

    @GetMapping("/all")
//    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PreAuthorize("hasAnyAuthority('librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllTransactions(@RequestBody PaginationRequest paginationRequest){
        return ResponseEntity.ok(ApiResponse
                .success(transactionService.getAllTransactions(paginationRequest)));
    }
}
