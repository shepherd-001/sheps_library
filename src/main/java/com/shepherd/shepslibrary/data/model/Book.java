package com.shepherd.shepslibrary.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Book extends BaseModel{
    private String title;
    private String author;
    private String genre;
    @Column(unique = true)
    private String isbn;
    private boolean isAvailable = true;
}
