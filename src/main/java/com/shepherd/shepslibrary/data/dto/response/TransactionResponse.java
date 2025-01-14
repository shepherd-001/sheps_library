package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
public class TransactionResponse {
    private UUID transactionId;
    private TransactionType transactionType;
    private String firstName;
    private String lastName;
    private String title;
    private String author;
    private String genre;
    private LocalDate borrowedDate;
    private LocalDate returnDate;
}
