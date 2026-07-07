package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.service.book.BookService;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
@Validated
public class BookController {
    private final BookService bookService;

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.create')")
    public ResponseEntity<ApiResponse<?>> addBook(@Valid @RequestBody AddBookRequest addBookRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse
                        .of("Book added successfully", bookService.addBook(addBookRequest)));
    }

    @GetMapping("/{bookId}")
    @PreAuthorize("hasAnyAuthority('admin.read', 'librarian.read', 'member.read')")
    public ResponseEntity<ApiResponse<?>> getBookById(@PathVariable UUID bookId) {
        return ResponseEntity.ok(ApiResponse
                .of(bookService.getBookById(bookId)));
    }

    @GetMapping("/isbn/{isbn}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PreAuthorize("hasAnyAuthority('admin.read', 'librarian.read')")
    public ResponseEntity<ApiResponse<?>> getBookByIsbn(@PathVariable
                                                        @Pattern(message = ValidationMessage.INVALID_ISBN, regexp = RegexPattern.ISBN)
                                                        @NotBlank(message = ValidationMessage.BLANK_ISBN)
                                                        String isbn) {
        return ResponseEntity.ok(ApiResponse.of(bookService.getBookByIsbn(isbn)));
    }

    @PutMapping("/{bookId}")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.update')")
    public ResponseEntity<ApiResponse<?>> updateBook(@Valid @RequestBody UpdateBookRequest updateBookRequest,
                                                     @PathVariable UUID bookId) {
        return ResponseEntity.ok(ApiResponse
                .of("Book updated successfully", bookService.updateBook(updateBookRequest, bookId)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin.read', 'librarian.read', 'member.read')")
    public ResponseEntity<ApiResponse<?>> getAllBooks(@RequestParam(required = false, defaultValue = "1") int page,
                                                      @RequestParam(required = false, defaultValue = "10") int size,
                                                      @RequestParam(required = false) String sort,
                                                      @RequestParam(required = false) String direction) {
        PaginationRequest paginationRequest = PaginationRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse.of(bookService.getAllBooks(paginationRequest)));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('admin.read', 'librarian.read', 'member.read')")

    public ResponseEntity<ApiResponse<?>> filterBook(@RequestParam(required = false) String title,
                                                     @RequestParam(required = false) String author,
                                                     @RequestParam(required = false) String genre,
                                                     @RequestParam(required = false, defaultValue = "1") int page,
                                                     @RequestParam(required = false, defaultValue = "10") int size,
                                                     @RequestParam(required = false) String sort,
                                                     @RequestParam(required = false) String direction) {
        FilterBookRequest filterBookRequest = new FilterBookRequest(title, author, genre);
        PaginationRequest paginationRequest = PaginationRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse.of(bookService.filterBook(filterBookRequest, paginationRequest)));
    }

    @DeleteMapping("/{bookId}")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.delete')")
    public ResponseEntity<ApiResponse<?>> deleteBook(@PathVariable UUID bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
