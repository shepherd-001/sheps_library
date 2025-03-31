package com.shepherd.shepslibrary.mapper;

import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralConfig.class)
public interface BookMapper {

    @Mapping(target = "title", expression = "java(request.getTitle().trim())")
    @Mapping(target = "author", expression = "java(request.getAuthor().trim())")
    @Mapping(target = "genre", expression = "java(request.getGenre().trim())")
    @Mapping(target = "isAvailable", constant = "true")
    @Mapping(target = "isbn", ignore = true)
    @Mapping(target = "book.createdBy", ignore = true)
    Book mapToBook(AddBookRequest request);


    @Mapping(target = "bookId", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "genre", source = "genre")
    @Mapping(target = "isbn", source = "isbn")
    @Mapping(target = "isAvailable", source = "available")
    AddBookResponse mapToAddBookResponse(Book book);


    @Mapping(target = "title", source = "title")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "genre", source = "genre")
    @Mapping(target = "isbn", source = "isbn")
    @Mapping(target = "isAvailable", source = "available")
    BookResponse mapToBookResponse(Book book);


    @Mapping(target = "title", expression = "java(request.getTitle().trim())")
    @Mapping(target = "author", expression = "java(request.getAuthor().trim())")
    @Mapping(target = "genre", expression = "java(request.getGenre().trim())")
    @Mapping(target = "updatedBy", ignore = true)
    void updateBookFromRequest(UpdateBookRequest request, @MappingTarget Book book);
}