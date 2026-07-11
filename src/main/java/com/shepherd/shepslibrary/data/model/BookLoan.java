package com.shepherd.shepslibrary.data.model;

import com.shepherd.shepslibrary.data.model.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BookLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
//    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id", nullable = false)
    private BookCopy copy;
    private Instant borrowedAt;
    private Instant dueDate;
    private Instant returnedAt;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
}
