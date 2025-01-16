package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.CreatePasswordRequest;
import com.shepherd.shepslibrary.service.librarian.LibrarianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Object> createPassword(@Valid @RequestBody CreatePasswordRequest createPasswordRequest){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(librarianService.createPassword(createPasswordRequest)));
    }
}
