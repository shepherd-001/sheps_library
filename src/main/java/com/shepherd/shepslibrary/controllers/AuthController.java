package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody RegisterUserRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.buildResponse(authService.registerUser(registerRequest)));
    }
}
