package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateBookRequest {
    private UUID bookId;

    @NotBlank(message = ValidationMessage.BLANK_TITLE)
    @Pattern(regexp = RegexPattern.BOOK_TITLE, message = ValidationMessage.INVALID_TITLE)
    private String title;

    @NotBlank(message = ValidationMessage.BLANK_AUTHOR)
    @Pattern(regexp = RegexPattern.BOOK_AUTHOR, message = ValidationMessage.INVALID_AUTHOR)
    private String author;

    @NotBlank(message = ValidationMessage.BLANK_GENRE)
    @Pattern(regexp = RegexPattern.BOOK_GENRE, message = ValidationMessage.INVALID_GENRE)
    private String genre;
}
