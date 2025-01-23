package com.shepherd.shepslibrary.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Transaction extends BaseModel{
    @ManyToOne
    private User user;
    @ManyToOne
    private Book book;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
