package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.common.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.common.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.common.exceptions.ShepsLibraryException;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "title", "author", "genre");
    private static final String BOOK_CACHE = "bookCache";

    @Override
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
        int maxAttempts = AppUtils.MAX_ISBN_ATTEMPTS;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            book.setIsbn(AppUtils.generateISBN());
            try {
                return bookRepository.save(book);
            } catch (DataIntegrityViolationException ex) {
                log.warn("==>> ISBN conflict on attempt {} with isbn='{}'. Retrying... Root cause: {}",
                        attempt, book.getIsbn(), ex.getMostSpecificCause().getMessage());
            }
        }
        throw new ShepsLibraryException("Failed to generate unique ISBN after %d attempts".formatted(maxAttempts));
    }


    @Override
    @Cacheable(value = BOOK_CACHE, key = "#bookId", unless = "#result == null")
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
    @Cacheable(value = BOOK_CACHE, key = "#isbn", unless = "#result == null")
    public BookResponse getBookByIsbn(String isbn) {
        log.info("==>> Fetching book by isbn");
        return bookRepository.findByIsbn(isbn)
                .map(bookMapper::mapToBookResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Book with the provided ISBN not found"));
    }

    @Override
    @Transactional
    @CachePut(value = BOOK_CACHE, key = "#bookId")
    public BookResponse updateBook(UpdateBookRequest updateBookRequest, UUID bookId) {
        Book book = fetchBookById(bookId);
        bookMapper.updateBookFromRequest(updateBookRequest, book);
        Book savedBook = bookRepository.save(book);
        log.info("==>> Updated book with title '{}'", book.getTitle());
        return bookMapper.mapToBookResponse(savedBook);
    }

    @Override
    @Cacheable(
            value = BOOK_CACHE,
            key = "#paginationRequest.toCacheKey('books')",
            unless = "#result == null || #result.items.isEmpty() ||  #paginationRequest.page > 5"
    )
    public PaginationResponse<BookResponse> getAllBooks(PaginationRequest paginationRequest) {
        Pageable pageable = paginationRequest.toPageable(ALLOWED_SORT_FIELDS);
        Page<Book> books = bookRepository.findAll(pageable);
        log.info("==>> All books fetched");
        return PaginationResponse.map(books, bookMapper::mapToBookResponse);
    }

    @Override
    @Cacheable(value = BOOK_CACHE,
            key = "#paginationRequest.toCacheKey('filter:title:'+#filterBookRequest.title+" +
                    "':author:'+#filterBookRequest.author+':genre:'+#filterBookRequest.genre)",
            unless = "#result == null || #result.items.isEmpty() || #paginationRequest.page > 5")
    public PaginationResponse<BookResponse> filterBook(FilterBookRequest filterBookRequest, PaginationRequest paginationRequest) {
        Pageable pageable = paginationRequest.toPageable(ALLOWED_SORT_FIELDS);
        Specification<Book> bookSpecification = Specification.where(
                        BookSpecification.hasTitle(filterBookRequest.title()))
                .and(BookSpecification.hasAuthor(filterBookRequest.title()))
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
    @CacheEvict(value = BOOK_CACHE, key = "#bookId")
    public void deleteBook(UUID bookId) {
        int deleted = bookRepository.deleteByIdReturningCount(bookId);
        if (deleted == 0)
            log.warn("==>> Attempted to delete a non-existing book");
        else log.info("==>> Book deleted successfully");
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
}