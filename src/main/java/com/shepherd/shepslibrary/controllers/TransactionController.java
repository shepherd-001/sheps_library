package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.service.transaction.TransactionService;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
        return ResponseEntity.ok(BaseResponse
                .buildResponse(transactionService.borrowBook(borrowBookRequest)));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/return-book")
    public ResponseEntity<Object> returnBook(@RequestParam UUID transactionId){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(transactionService.returnBook(transactionId)));
    }

    @GetMapping("/get/all/{userId}")
    public ResponseEntity<Object> getAllTransactions(@PathVariable UUID userId, @RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(transactionService.getAllTransactionByUserId(userId, pageNumber)));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Object> getAllTransactions(@RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(transactionService.getAllTransactions(pageNumber)));
    }
}
