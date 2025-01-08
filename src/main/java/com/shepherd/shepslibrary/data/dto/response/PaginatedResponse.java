package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PaginatedResponse<T> {
    private List<T> content;
    private int numberOfElements;
    private int totalPages;
    private long totalElements;
    private boolean last;
}
