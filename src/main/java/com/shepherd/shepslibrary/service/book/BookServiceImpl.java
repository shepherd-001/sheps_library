package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.repository.BookRepository;
import com.shepherd.shepslibrary.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.mapper.BookMapper;
import com.shepherd.shepslibrary.specification.BookSpecification;
import com.shepherd.shepslibrary.utils.AppUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

import static com.shepherd.shepslibrary.utils.AppUtils.NUMBER_OF_ITEMS_PER_PAGE;
import static com.shepherd.shepslibrary.utils.AppUtils.SORT_BY_CREATED_AT;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public AddBookResponse addBook(AddBookRequest request) {
        if(bookRepository.existsByTitle(request.getTitle()))
            throw new AlreadyExistsException("Book with title '%s' already exists".formatted(request.getTitle()));
        Book book = bookMapper.mapToBook(request);
//        todo cater for the uniqueness of ISBN generated.
        book.setIsbn(AppUtils.generateISBN());
        Book savedBook = bookRepository.save(book);
        log.info("::::: New book added :::::");
        return bookMapper.mapToAddBookResponse(savedBook);
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
        log.info("::::: Updated a book :::::");
        return bookMapper.mapToBookResponse(savedBook);
    }

    @Override
    public PaginatedResponse<BookResponse> getAllBooks(int pageNumber) {
        log.info("::::: Fetching all books :::::");
        Pageable pageable = findAllBooksPageRequest(pageNumber);
        Page<Book> books = bookRepository.findAll(pageable);
        return buildPaginatedBookResponse(books);
    }

    private Pageable findAllBooksPageRequest(int pageNumber) {
        return AppUtils.createPageRequest(pageNumber, NUMBER_OF_ITEMS_PER_PAGE, SORT_BY_CREATED_AT, Sort.Direction.ASC);
    }

    private PaginatedResponse<BookResponse> buildPaginatedBookResponse(Page<Book> books) {
        List<BookResponse> content = books.isEmpty() ? Collections.emptyList() :
                books.stream().map(bookMapper::mapToBookResponse).toList();
        return PaginatedResponse.<BookResponse>builder()
                .content(content)
                .numberOfElements(books.getNumberOfElements())
                .totalPages(books.getTotalPages())
                .totalElements(books.getTotalElements())
                .last(books.isLast())
                .build();
    }

    @Override
    @Cacheable(value = "bookCache", key = "#request.title + ':' + #request.author + ':' + #request.genre + ':' + #request.pageNumber")
    public PaginatedResponse<BookResponse> filterBook(FilterBookRequest request) {
        log.info(":::::  Filtering book :::::");
        Pageable pageable = findAllBooksPageRequest(request.getPageNumber());
        Specification<Book> bookSpecification = Specification.where(
                BookSpecification.hasTitle(request.getTitle()))
                .and(BookSpecification.hasAuthor(request.getAuthor()))
                .and(BookSpecification.hasGenre(request.getGenre()));
        Page<Book> books = bookRepository.findAll(bookSpecification, pageable);
        return buildPaginatedBookResponse(books);
    }

    @Override
    @Transactional
    @CacheEvict(value = "bookCache", key = "bookId")
//    @CacheEvict(value = "bookCache", allEntries = true)
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
