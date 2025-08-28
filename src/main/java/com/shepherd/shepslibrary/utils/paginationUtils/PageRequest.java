package com.shepherd.shepslibrary.utils.paginationUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageRequest {
    private int pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;
}