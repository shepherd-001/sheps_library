package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.UpdateBookResponse;
import com.shepherd.shepslibrary.data.model.Book;

import java.util.UUID;

public interface BookService {
    BaseResponse<AddBookResponse> addBook(AddBookRequest request);
    BaseResponse<BookResponse> getBookById(UUID id);
    Book fetchBookById(UUID bookId);
    BaseResponse<BookResponse> getBookByIsbn(String isbn);
    BaseResponse<UpdateBookResponse> updateBook(UpdateBookRequest request);
    BaseResponse<PaginatedResponse<BookResponse>> getAllBooks(int pageNumber);
    BaseResponse<PaginatedResponse<BookResponse>> filterBook(FilterBookRequest request);
    BaseResponse<String> deleteBook(UUID id);
    Book saveBook(Book book);
}
