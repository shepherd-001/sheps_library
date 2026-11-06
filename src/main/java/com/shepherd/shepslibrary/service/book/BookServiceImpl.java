package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.common.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.common.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.repository.BookRepository;
import com.shepherd.shepslibrary.mapper.BookMapper;
import com.shepherd.shepslibrary.specification.BookSpecification;
import com.shepherd.shepslibrary.utils.AppUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "title", "author", "genre");

    @Override
    @Transactional
    public AddBookResponse addBook(AddBookRequest request) {
        if (bookRepository.existsByTitle(request.title().trim())) {
            throw new AlreadyExistsException("Book with title '%s' already exists".formatted(request.title()));
        }

        Book book = bookMapper.mapToBook(request);
        book.setAvailable(true);
        Book savedBook = saveBookWithUniqueIsbn(book);
        log.info("==>> Book with title='{}' added successfully", savedBook.getTitle());
        return bookMapper.mapToAddBookResponse(savedBook);
    }

    private Book saveBookWithUniqueIsbn(Book book) {
        book.setIsbn(AppUtils.generateISBN());
        // A constraint failure marks this transaction rollback-only. Let it roll back;
        // retrying save in the same transaction cannot safely recover.
        return bookRepository.saveAndFlush(book);
    }


    @Override
    public BookResponse getBookById(UUID bookId) {
        Book book = fetchBookById(bookId);
        log.info("==>> Fetched book by id");
        return bookMapper.mapToBookResponse(book);
    }

    @Override
    public Book fetchBookById(UUID bookId) {
        return bookRepository.findById(bookId).orElseThrow
                (() -> new ResourceNotFoundException("Book with the provided ID not found"));
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        log.info("==>> Fetching book by isbn");
        return bookRepository.findByIsbn(isbn)
                .map(bookMapper::mapToBookResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Book with the provided ISBN not found"));
    }

    @Override
    @Transactional
    public BookResponse updateBook(UpdateBookRequest updateBookRequest, UUID bookId) {
        Book book = bookRepository.findByIdForUpdate(bookId).orElseThrow(
                () -> new ResourceNotFoundException("Book not found"));
        bookMapper.updateBookFromRequest(updateBookRequest, book);
        Book savedBook = bookRepository.save(book);
        log.info("==>> Updated book with title '{}'", book.getTitle());
        return bookMapper.mapToBookResponse(savedBook);
    }

    @Override
    public PaginationResponse<BookResponse> getAllBooks(PaginationRequest paginationRequest) {
        Pageable pageable = paginationRequest.toPageable(ALLOWED_SORT_FIELDS);
        Page<Book> books = bookRepository.findAll(pageable);
        log.info("==>> All books fetched");
        return PaginationResponse.map(books, bookMapper::mapToBookResponse);
    }

    @Override
    public PaginationResponse<BookResponse> filterBook(FilterBookRequest filterBookRequest, PaginationRequest paginationRequest) {
        Pageable pageable = paginationRequest.toPageable(ALLOWED_SORT_FIELDS);
        Specification<Book> bookSpecification = Specification.where(
                        BookSpecification.hasTitle(filterBookRequest.title()))
                .and(BookSpecification.hasAuthor(filterBookRequest.author()))
                .and(BookSpecification.hasGenre(filterBookRequest.genre()));
        Page<Book> books = bookRepository.findAll(bookSpecification, pageable);
        log.info("Books filtered successfully with title={}, author={}, genre={}",
                filterBookRequest.title(),
                filterBookRequest.author(),
                filterBookRequest.genre());
        return PaginationResponse.map(books, bookMapper::mapToBookResponse);
    }

    @Override
    @Transactional
    public void deleteBook(UUID bookId) {
        int deleted = bookRepository.deleteByIdReturningCount(bookId);
        if (deleted == 0)
            log.warn("==>> Attempted to delete a non-existing book");
        else log.info("==>> Book deleted successfully");
    }

    @Override
    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
}