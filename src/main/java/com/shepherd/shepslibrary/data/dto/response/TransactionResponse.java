package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class TransactionResponse {
    private String transactionId;
    private TransactionType transactionType;
    private String firstName;
    private String lastName;
    private String title;
    private String author;
    private String genre;
    private LocalDateTime borrowedDateTime;
    private LocalDateTime returnDateTime;
}
