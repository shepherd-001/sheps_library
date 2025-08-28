package com.shepherd.shepslibrary.utils.paginationUtils;

import com.shepherd.shepslibrary.utils.ErrorMessage;
import org.springframework.data.domain.Page;

public final class PageMapper {

    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber() + 1) // 1 based page number
                .pageSize(page.getSize())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .isLast(page.isLast())
                .build();
    }

    private PageMapper() {
        throw new UnsupportedOperationException(ErrorMessage.NON_INSTANTIABLE_UTILITY_CLASS);
    }
}
