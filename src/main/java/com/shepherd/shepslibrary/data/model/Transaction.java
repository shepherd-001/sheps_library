package com.shepherd.shepslibrary.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_createdAt", columnList = "createdAt")
})
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
