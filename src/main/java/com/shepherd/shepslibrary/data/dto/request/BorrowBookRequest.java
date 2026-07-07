package com.shepherd.shepslibrary.data.dto.request;

import java.time.Instant;
import java.util.UUID;

public record BorrowBookRequest(
        UUID bookId,
        Instant returnDateTime
) {
}
