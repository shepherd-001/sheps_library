package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.shepherd.shepslibrary.utils.ValidationMessage.*;

public record AddBookRequest(
    @NotBlank(message = ValidationMessage.BLANK_TITLE)
    @Pattern(regexp = RegexPattern.BOOK_TITLE, message = ValidationMessage.INVALID_TITLE)
    @Size(max = 100, message = TITLE_TOO_LONG)
    String title,

    @NotBlank(message = ValidationMessage.BLANK_AUTHOR)
    @Pattern(regexp = RegexPattern.BOOK_AUTHOR, message = ValidationMessage.INVALID_AUTHOR)
    @Size(max = 100, message = AUTHOR_NAME_TOO_LONG)
    String author,

    @NotBlank(message = ValidationMessage.BLANK_GENRE)
    @Pattern(regexp = RegexPattern.BOOK_GENRE, message = ValidationMessage.INVALID_GENRE)
    @Size(max = 20, message = GENRE_TOO_LONG)
    String genre
){}
