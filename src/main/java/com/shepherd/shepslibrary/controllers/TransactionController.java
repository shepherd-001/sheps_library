package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.security.AuthenticatedUser;
import com.shepherd.shepslibrary.service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/borrow-book")
    @PreAuthorize("hasAuthority('member.create')")
    public ResponseEntity<ApiResponse<?>> borrowBook(@Valid @RequestBody BorrowBookRequest borrowBookRequest,
                                                     @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(ApiResponse.of("Book borrowed successfully",
                transactionService.borrowBook(borrowBookRequest, authenticatedUser)));
    }

    @PutMapping("/return-book")
    @PreAuthorize("hasAuthority('member.update')")
    public ResponseEntity<ApiResponse<?>> returnBook(@RequestParam UUID transactionId) {
        return ResponseEntity.ok(ApiResponse.of("Book returned successfully",
                transactionService.returnBook(transactionId)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('member.read')")
    public ResponseEntity<ApiResponse<?>> getMyTransactions(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                             @RequestParam(required = false, defaultValue = "1") int page,
                                                             @RequestParam(required = false, defaultValue = "10") int size,
                                                             @RequestParam(required = false) String sort,
                                                             @RequestParam(required = false) String direction) {
        PaginationRequest paginationRequest = PaginationRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse
                .of(transactionService.getAllTransactionByUserId(authenticatedUser.getUser().getId(), paginationRequest)));
    }

    @GetMapping("/all/{userId}")
    @PreAuthorize("hasAnyAuthority('librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllTransactions(@PathVariable UUID userId,
                                                             @RequestParam(required = false, defaultValue = "1") int page,
                                                             @RequestParam(required = false, defaultValue = "10") int size,
                                                             @RequestParam(required = false) String sort,
                                                             @RequestParam(required = false) String direction) {
        PaginationRequest paginationRequest = PaginationRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse
                .of(transactionService.getAllTransactionByUserId(userId, paginationRequest)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllTransactions(@RequestParam(required = false, defaultValue = "1") int page,
                                                             @RequestParam(required = false, defaultValue = "10") int size,
                                                             @RequestParam(required = false) String sort,
                                                             @RequestParam(required = false) String direction) {
        PaginationRequest paginationRequest = PaginationRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse
                .of(transactionService.getAllTransactions(paginationRequest)));
    }
}
