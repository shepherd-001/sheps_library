package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginationResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.repository.BookRepository;
import com.shepherd.shepslibrary.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
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

import java.util.Collections;
import java.util.List;

import static com.shepherd.shepslibrary.utils.AppUtils.createPageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public AddBookResponse addBook(AddBookRequest request) {
        if (bookRepository.existsByTitle(request.getTitle().trim())) {
            throw new AlreadyExistsException("Book with title '%s' already exists".formatted(request.getTitle()));
        }

        Book book = bookMapper.mapToBook(request);
        Book savedBook = saveBookWithUniqueIsbn(book);
        log.info("::::: Book added with title='{}' :::::", savedBook.getTitle());

        return bookMapper.mapToAddBookResponse(savedBook);
    }

    private Book saveBookWithUniqueIsbn(Book book) {
        int maxAttempts = AppUtils.MAX_ISBN_ATTEMPTS;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            book.setIsbn(AppUtils.generateISBN());
            try {
                return bookRepository.save(book);
            } catch (DataIntegrityViolationException ex) {
                log.warn("ISBN conflict on attempt {} with isbn='{}'. Retrying... Root cause: {}",
                        attempt, book.getIsbn(), ex.getMostSpecificCause().getMessage());
            }
        }
        throw new ShepsLibraryException("Failed to generate unique ISBN after %d attempts".formatted(maxAttempts));
    }


    @Override
    @Cacheable(value = "bookCache", key = "#bookId", unless = "#result == null")
    public BookResponse getBookById(String bookId) {
        log.info("::::: Fetching book by id :::::");
        Book book = fetchBookById(bookId);
        return bookMapper.mapToBookResponse(book);
    }

    @Override
    public Book fetchBookById(String bookId) {
        return bookRepository.findById(bookId).orElseThrow
                (()-> new ResourceNotFoundException("Book with the provided ID not found"));
    }

    @Override
    @Cacheable(value = "bookCache", key = "#isbn", unless = "#result == null")
    public BookResponse getBookByIsbn(String isbn) {
        log.info("::::: Fetching book by isbn :::::");
        return bookRepository.findByIsbn(isbn)
                .map(bookMapper::mapToBookResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Book with the provided ISBN not found"));
    }

    @Override
    @Transactional
    @CachePut(value = "bookCache", key = "#bookId")
    public BookResponse updateBook(UpdateBookRequest updateBookRequest, String bookId) {
        Book book = fetchBookById(bookId);
        bookMapper.updateBookFromRequest(updateBookRequest, book);
        Book savedBook = bookRepository.save(book);
        log.info("::::: Updated book with title '{}' :::::", book.getTitle());
        return bookMapper.mapToBookResponse(savedBook);
    }

    @Override
    @Cacheable(
            value = "bookCache",
            key = "#paginationRequest.toCacheKey('books')",
            unless = "#result == null || #result.content.isEmpty()"
    )
    public PaginationResponse<BookResponse> getAllBooks(PaginationRequest paginationRequest) {
        log.info("::::: Fetching all books :::::");
        Pageable pageable = createPageRequest(paginationRequest.getPage(), paginationRequest.getSize(),
                paginationRequest.getSort(), paginationRequest.getDirection());
        Page<Book> books = bookRepository.findAll(pageable);
        return mapToPaginatedBookResponse(books);
    }

    private PaginationResponse<BookResponse> mapToPaginatedBookResponse(Page<Book> books) {
        List<BookResponse> content = books.isEmpty() ? Collections.emptyList() :
                books.stream().map(bookMapper::mapToBookResponse).toList();
        return PaginationResponse.<BookResponse>builder()
                .content(content)
                .page(books.getNumber())
                .size(books.getSize())
                .numberOfElements(books.getNumberOfElements())
                .totalElements(books.getTotalElements())
                .totalPages(books.getTotalPages())
                .hasNext(books.hasNext())
                .hasPrevious(books.hasPrevious())
                .last(books.isLast())
                .build();
    }

    @Override
    @Cacheable(value = "bookCache", key = "#request.toCacheKey()",
            unless = "#result == null || #result.content.isEmpty()")
    public PaginationResponse<BookResponse> filterBook(FilterBookRequest request) {
        log.info(":::::  Filtering book :::::");
        Pageable pageable = createPageRequest(request.getPageNumber(), request.getPageSize(),
                request.getSortBy(), request.getSortDirection());
        Specification<Book> bookSpecification = Specification.where(
                BookSpecification.hasTitle(request.getTitle()))
                .and(BookSpecification.hasAuthor(request.getAuthor()))
                .and(BookSpecification.hasGenre(request.getGenre()));
        Page<Book> books = bookRepository.findAll(bookSpecification, pageable);
        return mapToPaginatedBookResponse(books);
    }

    @Override
    @Transactional
    @CacheEvict(value = "bookCache", key = "#bookId")
    public String deleteBook(String bookId) {
        if(!bookRepository.existsById(bookId))
            throw new ResourceNotFoundException("Book with the provided ID not found");
        bookRepository.deleteById(bookId);
        log.info("::::: Deleted a book by id :::::");
        return "Book deleted successfully";
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
}
