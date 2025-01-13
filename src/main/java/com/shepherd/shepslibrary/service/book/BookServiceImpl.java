package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.UpdateBookResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.repository.BookRepository;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public AddBookResponse addBook(AddBookRequest request) {
        Book book = new Book();
        String createdBy = AppUtils.getCurrentUser().getEmail();
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setGenre(request.getGenre().trim());
        book.setIsbn(generateRandomIsbn());
        book.setCreatedBy(createdBy);
        Book savedBook = bookRepository.save(book);
        log.info("::::: New book added :::::");
        return AddBookResponse.builder()
                .message("Book added successfully")
                .bookId(savedBook.getId())
                .title(savedBook.getTitle())
                .author(savedBook.getAuthor())
                .genre(savedBook.getGenre())
                .isbn(savedBook.getIsbn())
                .isAvailable(savedBook.isAvailable())
                .build();
    }

    private String generateRandomIsbn() {
        SecureRandom secureRandom = new SecureRandom();
        int[] values = {978, 979};
        int randomIndex = secureRandom.nextInt(values.length);

        String suffix = secureRandom.ints(10, 1, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());

        String isbn = String.format("%d%s", values[randomIndex], suffix);
        log.info("::::: Generated new ISBN: {} :::::", isbn);
        return isbn;
    }

    @Override
    public BookResponse getBookById(UUID id) {
        log.info("::::: Fetching book by id :::::");
        return mapToBookResponse(findBookById(id));
    }

    private Book findBookById(UUID bookId) {
        return bookRepository.findById(bookId).orElseThrow
                (()-> new ResourceNotFoundException("Book with the provided ID not found"));
    }
    @Override
    public BookResponse getBookByIsbn(String isbn) {
        log.info("::::: Fetching book by isbn :::::");
        return bookRepository.findByIsbn(isbn)
                .map(this::mapToBookResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Book with the provided ISBN not found"));
    }

    private BookResponse mapToBookResponse(Book book){
        return BookResponse.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .genre(book.getGenre())
                .isbn(book.getIsbn())
                .isAvailable(book.isAvailable())
                .build();
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookRequest request) {
        Book book = findBookById(request.getBookId());
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setGenre(request.getGenre().trim());
        book.setUpdatedBy(AppUtils.getCurrentUser().getEmail());
        Book savedBook = bookRepository.save(book);
        log.info("::::: Updated a book :::::");
        return UpdateBookResponse.builder()
                .message("Book updated successfully")
                .title(savedBook.getTitle())
                .author(savedBook.getAuthor())
                .genre(savedBook.getGenre())
                .isbn(savedBook.getIsbn())
                .isAvailable(savedBook.isAvailable())
                .build();
    }

    @Override
    public PaginatedResponse<BookResponse> getAllBooks(int pageNumber) {
        return null;
    }

    @Override
    public PaginatedResponse<BookResponse> filterBook(String searchRequest) {
        return null;
    }

    @Override
    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
        log.info("::::: Deleted a book by id :::::");
    }
}
