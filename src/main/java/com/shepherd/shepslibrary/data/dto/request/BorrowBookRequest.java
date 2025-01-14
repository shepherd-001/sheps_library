package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BorrowBookRequest {
    private UUID bookId;
    private LocalDate returnDate;
}
