package com.shepherd.shepslibrary.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.shepherd.shepslibrary.utils.AppUtils.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationRequest {
    private int page;
    private int size;
    private String sort;
    private String direction;


    public String toCacheKey(String prefix) {
        return String.format(
                "%s:page:%d:size:%s:sort:%s:%s",
                prefix,
                resolvedPageNumber(page),
                resolvedPageSize(size),
                resolvedSortBy(sort),
                resolvedSortDirection(direction)
        );
    }
}
