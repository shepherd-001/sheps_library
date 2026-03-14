package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.AppUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationRequest {
    private int page;
    private Integer size;
    private String sort;
    private String direction;


    public String toCacheKey(String prefix) {
        return String.format(
                "%s:page:%d:size:%s:sortBy:%s:direction:%s",
                prefix,
                page,
                size != null ? size : AppUtils.DEFAULT_PAGE_SIZE,
                sort != null ? sort.toLowerCase() : AppUtils.SORT_BY_CREATED_AT,
                direction != null ? direction.toUpperCase() : AppUtils.SORT_DIRECTION_ASC
        );
    }
}
