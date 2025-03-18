package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/borrow-book")
    public ResponseEntity<Object> borrowBook(@Valid @RequestBody BorrowBookRequest borrowBookRequest){
        return ResponseEntity.ok(transactionService.borrowBook(borrowBookRequest));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/return-book")
    public ResponseEntity<Object> returnBook(@RequestParam UUID transactionId){
        return ResponseEntity.ok(transactionService.returnBook(transactionId));
    }

    @GetMapping("/get/all/{userId}")
    public ResponseEntity<Object> getAllTransactions(@PathVariable UUID userId, @RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(transactionService.getAllTransactionByUserId(userId, pageNumber));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Object> getAllTransactions(@RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(transactionService.getAllTransactions(pageNumber));
    }
}
