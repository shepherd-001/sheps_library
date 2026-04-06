package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record UpdateBookRequest(
        @NotBlank(message = ValidationMessage.BLANK_TITLE)
        @Pattern(regexp = RegexPattern.BOOK_TITLE, message = ValidationMessage.INVALID_TITLE)
        String title,

        @NotBlank(message = ValidationMessage.BLANK_AUTHOR)
        @Pattern(regexp = RegexPattern.BOOK_AUTHOR, message = ValidationMessage.INVALID_AUTHOR)
        String author,

        @NotBlank(message = ValidationMessage.BLANK_GENRE)
        @Pattern(regexp = RegexPattern.BOOK_GENRE, message = ValidationMessage.INVALID_GENRE)
        String genre
) {}
