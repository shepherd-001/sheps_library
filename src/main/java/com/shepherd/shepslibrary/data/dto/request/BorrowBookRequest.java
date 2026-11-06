package com.shepherd.shepslibrary.data.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record BorrowBookRequest(
//        @jakarta.validation.constraints.NotNull UUID bookId,
//        @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Future Instant returnDateTime
        @NotNull
        UUID bookId,
        @NotNull
        Instant returnDateTime
) {
}
