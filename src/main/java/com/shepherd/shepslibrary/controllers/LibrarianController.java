package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.CreatePasswordRequest;
import com.shepherd.shepslibrary.service.librarian.LibrarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/librarian")
@RequiredArgsConstructor
public class LibrarianController {
    private final LibrarianService librarianService;


    @PostMapping("/create-password")
    public ResponseEntity<ApiResponse<?>> createPassword(@Valid @RequestBody CreatePasswordRequest createPasswordRequest){
        return ResponseEntity.ok(ApiResponse
                .buildResponse("Librarian password created successfully", librarianService.createPassword(createPasswordRequest)));
    }
}
