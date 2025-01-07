package com.shepherd.shepslibrary.data.model;

import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

public class Transaction extends BaseModel{
    @ManyToOne
    private User user;
    @ManyToOne
    private Book book;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
