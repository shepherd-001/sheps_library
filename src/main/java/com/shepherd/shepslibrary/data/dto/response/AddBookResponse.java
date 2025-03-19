package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class AddBookResponse {
    private UUID bookId;
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private boolean isAvailable;
}
