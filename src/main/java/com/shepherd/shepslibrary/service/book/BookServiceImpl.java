package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.data.dto.response.AddBookResponse;
import com.shepherd.shepslibrary.data.dto.response.BookResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.UpdateBookResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.repository.BookRepository;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.specification.BookSpecification;
import com.shepherd.shepslibrary.utils.AppUtils;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static com.shepherd.shepslibrary.utils.AppUtils.NUMBER_OF_ITEMS_PER_PAGE;
import static com.shepherd.shepslibrary.utils.AppUtils.SORT_BY_CREATED_AT;

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
        book.setAvailable(true);
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
    @Cacheable(value = "bookCache", key = "#id")
    public BookResponse getBookById(UUID id) {
        log.info("::::: Fetching book by id :::::");
        return mapToBookResponse(fetchBookById(id));
    }

    @Override
    public Book fetchBookById(UUID id) {
        return bookRepository.findById(id).orElseThrow
                (()-> new ResourceNotFoundException("Book with the provided ID not found"));
    }

    @Override
    @Cacheable(value = "bookCache", key = "#isbn")
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
    @CachePut(value = "bookCache", key = "#request.bookId")
    public UpdateBookResponse updateBook(UpdateBookRequest request) {
        Book book = fetchBookById(request.getBookId());
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
                books.stream().map(this::mapToBookResponse).toList();
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
    @CacheEvict(value = "bookCache", allEntries = true)
    public void deleteBook(UUID bookId) {
        if(!bookRepository.existsById(bookId))
            throw new ResourceNotFoundException("Book with the provided ID not found");
        bookRepository.deleteById(bookId);
        log.info("::::: Deleted a book by id :::::");
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
}
