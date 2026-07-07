package com.shepherd.shepslibrary.data.dto.response;

public record BookResponse(
        String title,
        String author,
        String genre,
        String isbn,
        boolean available
){}
