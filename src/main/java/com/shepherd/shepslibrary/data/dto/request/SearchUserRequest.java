package com.shepherd.shepslibrary.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchUserRequest {
    private String searchText;
    private int pageNumber;
}
