package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PaginationResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private int numberOfElements;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean last;
}
