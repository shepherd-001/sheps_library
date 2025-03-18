package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateBookResponse {
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private boolean isAvailable;
}
