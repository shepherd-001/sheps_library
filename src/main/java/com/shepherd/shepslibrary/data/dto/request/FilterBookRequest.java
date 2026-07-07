package com.shepherd.shepslibrary.data.dto.request;

public record FilterBookRequest(
        String title,
        String author,
        String genre
) {}