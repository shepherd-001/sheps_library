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
public class FilterBookRequest {
    private String title;
    private String author;
    private String genre;

    private int pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;


    public String toCacheKey() {
        return String.format(
                "filter:title:%s:author:%s:genre:%s:page:%d:size:%d:sortBy:%s:direction:%s",
                title != null ? title.trim().toLowerCase() : "any",
                author != null ? author.trim().toLowerCase() : "any",
                genre != null ? genre.trim().toLowerCase() : "any",
                pageNumber,
                pageSize != null ? pageSize : AppUtils.DEFAULT_PAGE_SIZE,
                sortBy != null ? sortBy.toLowerCase() : AppUtils.SORT_BY_CREATED_AT,
                sortDirection != null ? sortDirection.toUpperCase() : AppUtils.SORT_DIRECTION_ASC
        );
    }
}
