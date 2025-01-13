package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.AddBookRequest;
import com.shepherd.shepslibrary.data.dto.request.UpdateBookRequest;
import com.shepherd.shepslibrary.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<Object> addBook(@Valid @RequestBody AddBookRequest addBookRequest){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.buildResponse(bookService.addBook(addBookRequest)));
    }

    @GetMapping("/get/{bookId}")
    public ResponseEntity<Object> getBookById(@PathVariable UUID bookId){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(bookService.getBookById(bookId)));
    }

    @GetMapping("/get/{isbn}")
    public ResponseEntity<Object> getBookByIsbn(@PathVariable String isbn){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(bookService.getBookByIsbn(isbn)));
    }

    @PutMapping("/edit")
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
    public ResponseEntity<Object> filterBook(@RequestParam String filter){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(bookService.filterBook(filter)));
    }

    @DeleteMapping("/delete/{bookId}")
    public ResponseEntity<Object> deleteBook(@PathVariable UUID bookId){
        bookService.deleteBook(bookId);
        return ResponseEntity.ok(BaseResponse.buildResponse("Book deleted successfully"));
    }
}
