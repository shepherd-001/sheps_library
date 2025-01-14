package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/borrow-book")
    public ResponseEntity<Object> borrowBook(@Valid @RequestBody BorrowBookRequest borrowBookRequest){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(transactionService.borrowBook(borrowBookRequest)));
    }

    @PutMapping("/return-book")
    public ResponseEntity<Object> returnBook(@RequestParam UUID transactionId){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(transactionService.returnBook(transactionId)));
    }

    @GetMapping("/get/all")
    public ResponseEntity<Object> getAllTransactions(@RequestParam int pageNumber){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(transactionService.getAllTransactions(pageNumber)));
    }
}
