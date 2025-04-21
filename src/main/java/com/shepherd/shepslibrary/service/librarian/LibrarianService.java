package com.shepherd.shepslibrary.service.librarian;

import com.shepherd.shepslibrary.data.dto.request.CreatePasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.AuthResponse;

public interface LibrarianService {
    AuthResponse createPassword(CreatePasswordRequest request);
}
