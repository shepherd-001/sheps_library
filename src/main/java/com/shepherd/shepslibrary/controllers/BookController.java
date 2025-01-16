package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> addBook(@Valid @RequestBody AddBookRequest addBookRequest){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.buildResponse(bookService.addBook(addBookRequest)));
    }

    @GetMapping("/get-by-id/{bookId}")
    public ResponseEntity<Object> getBookById(@PathVariable UUID bookId){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(bookService.getBookById(bookId)));
    }

    @GetMapping("/get-by-isbn/{isbn}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Object> getBookByIsbn(@PathVariable String isbn){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(bookService.getBookByIsbn(isbn)));
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateBook(@Valid @RequestBody UpdateBookRequest updateBookRequest){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(bookService.updateBook(updateBookRequest)));
    }

    @GetMapping("/get/all")
    public ResponseEntity<Object> getAllBooks(@RequestParam int pageNumber){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(bookService.getAllBooks(pageNumber)));
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> filterBook(@Valid @RequestBody FilterBookRequest filterBookRequest){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(bookService.filterBook(filterBookRequest)));
    }

    @DeleteMapping("/delete/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteBook(@PathVariable UUID bookId){
        bookService.deleteBook(bookId);
        return ResponseEntity.ok(BaseResponse.buildResponse("Book deleted successfully"));
    }
}
