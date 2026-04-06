package com.shepherd.shepslibrary.mapper;

import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = CentralConfig.class)
public interface BookMapper {

    @Mapping(target = "title", source = "title", qualifiedByName = "trim")
    @Mapping(target = "author", source = "author", qualifiedByName = "trim")
    @Mapping(target = "genre", source = "genre", qualifiedByName = "trim")
    @Mapping(target = "isbn", ignore = true)
    @Mapping(target = "book.createdBy", ignore = true)
    Book mapToBook(AddBookRequest request);

    AddBookResponse mapToAddBookResponse(Book book);

    BookResponse mapToBookResponse(Book book);

    @Mapping(target = "title", source = "title", qualifiedByName = "trim")
    @Mapping(target = "author", source = "author", qualifiedByName = "trim")
    @Mapping(target = "genre", source = "genre", qualifiedByName = "trim")
    @Mapping(target = "lastModifiedBy", ignore = true)
    void updateBookFromRequest(UpdateBookRequest request, @MappingTarget Book book);

    @Named("trim")
    default String trim(String value) {
        return value != null ? value.trim() : null;
    }
}
