package com.shepherd.shepslibrary.common.response;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;


public record PaginationResponse<T>(
        List<T> items,
        int page,
        int size,
        int numberOfElements,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        boolean first,
        boolean last
) {
    private static <T> PaginationResponse<T> of(
            Page<?> page,
            List<T> items
    ) {
        return new PaginationResponse<>(
                items,
                page.getNumber() + 1,
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.isFirst(),
                page.isLast()
        );
    }

    public static <S, T> PaginationResponse<T> map(Page<S> page, Function<S, T> mapper) {
        return of(
                page,
                page.stream().map(mapper).toList()
        );
    }


    public static <T> PaginationResponse<T> map(Page<T> page) {
        return of(page, page.getContent());
    }
}