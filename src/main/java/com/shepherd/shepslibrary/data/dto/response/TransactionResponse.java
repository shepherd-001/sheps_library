package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class TransactionResponse {
    private String message;
    private String firstName;
    private String lastName;
    private String title;
    private String author;
    private String genre;
    private LocalDate borrowedDate;
    private LocalDate returnDate;
}
