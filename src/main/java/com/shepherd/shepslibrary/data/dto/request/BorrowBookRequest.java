package com.shepherd.shepslibrary.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BorrowBookRequest {
    private String bookId;
    private LocalDate returnDate;
}
