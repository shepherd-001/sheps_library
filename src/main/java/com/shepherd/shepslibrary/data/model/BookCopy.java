package com.shepherd.shepslibrary.data.model;

import com.shepherd.shepslibrary.common.BaseEntity;
import com.shepherd.shepslibrary.data.model.enums.CopyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "book_copies")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BookCopy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String barcode;
    @Enumerated(EnumType.STRING)
    private CopyStatus status;
    private String shelfLocation;
    private Instant acquiredDatTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
