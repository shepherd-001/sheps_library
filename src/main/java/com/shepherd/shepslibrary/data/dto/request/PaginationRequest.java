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
    private int pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;


    public String toCacheKey(String prefix) {
        return String.format(
                "%s:page:%d:size:%s:sortBy:%s:direction:%s",
                prefix,
                pageNumber,
                pageSize != null ? pageSize : AppUtils.PAGE_SIZE,
                sortBy != null ? sortBy.toLowerCase() : AppUtils.SORT_BY_CREATED_AT,
                sortDirection != null ? sortDirection.toUpperCase() : AppUtils.SORT_DIRECTION_ASC
        );
    }
}
