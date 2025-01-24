package com.shepherd.shepslibrary.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_createdAt", columnList = "createdAt")
})
public class Book extends BaseModel{
    private String title;
    private String author;
    private String genre;
    @Column(unique = true)
    private String isbn;
    private boolean isAvailable;
}
