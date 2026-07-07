package com.shepherd.shepslibrary.data.dto.response;

import java.util.UUID;

public record AddBookResponse(
        UUID id,
        String title,
        String author,
        String genre,
        String isbn,
        boolean available
) {}
