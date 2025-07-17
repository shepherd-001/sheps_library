package com.shepherd.shepslibrary.data.model;

import com.shepherd.shepslibrary.data.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_createdAt", columnList = "createdAt")
})
public class Transaction extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private Instant borrowDateTime;
    private Instant returnDateTime;
}
