package com.shepherd.shepslibrary.data.dto.request;

import java.time.Instant;

public record BorrowBookRequest(
    String bookId,
    Instant returnDateTime
){}
