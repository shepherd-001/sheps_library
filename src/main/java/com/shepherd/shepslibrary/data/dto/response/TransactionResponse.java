package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.TransactionType;

import java.time.Instant;

public record TransactionResponse(
        String id,
        TransactionType transactionType,
        String firstName,
        String lastName,
        String title,
        String author,
        String genre,
        Instant borrowDateTime,
        Instant returnDateTime
) {}
