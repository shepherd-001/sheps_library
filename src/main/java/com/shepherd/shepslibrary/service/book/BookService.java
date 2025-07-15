package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.model.Book;

public interface BookService {
    AddBookResponse addBook(AddBookRequest request);
    BookResponse getBookById(String book);
    Book fetchBookById(String bookId);
    BookResponse getBookByIsbn(String isbn);
    BookResponse updateBook(UpdateBookRequest request, String bookId);
    PaginatedResponse<BookResponse> getAllBooks(int pageNumber);
    PaginatedResponse<BookResponse> filterBook(FilterBookRequest request);
    String deleteBook(String id);
    Book saveBook(Book book);
}
