package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.service.book.BookService;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
@Validated
public class BookController {
    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addBook(@Valid @RequestBody AddBookRequest addBookRequest){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse
                        .buildResponse("Book added successfully", bookService.addBook(addBookRequest)));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<?>> getBookById(@PathVariable String bookId){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(bookService.getBookById(bookId)));
    }

    @GetMapping("/isbn/{isbn}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<?>> getBookByIsbn(@PathVariable
                                                    @Pattern(message= ValidationMessage.INVALID_ISBN, regexp = RegexPattern.ISBN)
                                                    String isbn){
        if(isbn.isBlank())
            throw new ShepsLibraryException(ValidationMessage.BLANK_ISBN);
        return ResponseEntity.ok(ApiResponse.buildResponse(bookService.getBookByIsbn(isbn)));
    }

    @PutMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateBook(@Valid @RequestBody UpdateBookRequest updateBookRequest,
                                             @PathVariable String bookId){
        return ResponseEntity.ok(ApiResponse
                .buildResponse("Book updated successfully", bookService.updateBook(updateBookRequest, bookId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllBooks(@RequestParam(defaultValue = "0")
                                                  int pageNumber){
        return ResponseEntity.ok(ApiResponse.buildResponse(bookService.getAllBooks(pageNumber)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> filterBook(@Valid @RequestBody FilterBookRequest filterBookRequest){
        return ResponseEntity.ok(ApiResponse.buildResponse(bookService.filterBook(filterBookRequest)));
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteBook(@PathVariable String bookId){
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.buildResponse(bookService.deleteBook(bookId)));
    }
//
//    private static void checkBookNotBlank(UUID bookId) {
//        if(bookId == null)
//            throw new ShepsLibraryException(ValidationMessage.BLANK_BOOK_ID);
//    }
}
