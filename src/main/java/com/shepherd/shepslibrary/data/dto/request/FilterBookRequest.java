package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterBookRequest {
    private String title;
    private String author;
    private String genre;
    @Min(value = 0)
    @NotBlank(message = ValidationMessage.BLANK_PAGE_NUMBER)
    private int pageNumber;
}
