package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.model.Book;

public interface BookService {
    BaseResponse<AddBookResponse> addBook(AddBookRequest request);
    BaseResponse<BookResponse> getBookById(String id);
    Book fetchBookById(String bookId);
    BaseResponse<BookResponse> getBookByIsbn(String isbn);
    BaseResponse<BookResponse> updateBook(UpdateBookRequest request, String bookId);
    BaseResponse<PaginatedResponse<BookResponse>> getAllBooks(int pageNumber);
    BaseResponse<PaginatedResponse<BookResponse>> filterBook(FilterBookRequest request);
    BaseResponse<String> deleteBook(String id);
    Book saveBook(Book book);
}
