package com.shepherd.shepslibrary.service.book;

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
    AddBookResponse addBook(AddBookRequest request);
    BookResponse getBookById(UUID id);
    Book fetchBookById(UUID bookId);
    BookResponse getBookByIsbn(String isbn);
    UpdateBookResponse updateBook(UpdateBookRequest request);
    PaginatedResponse<BookResponse> getAllBooks(int pageNumber);
    PaginatedResponse<BookResponse> filterBook(FilterBookRequest request);
    void deleteBook(UUID id);
    Book saveBook(Book book);
}
