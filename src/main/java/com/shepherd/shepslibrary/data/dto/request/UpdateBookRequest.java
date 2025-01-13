package com.shepherd.shepslibrary.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateBookRequest {
    private UUID bookId;
    private String title;
    private String author;
    private String genre;
}
