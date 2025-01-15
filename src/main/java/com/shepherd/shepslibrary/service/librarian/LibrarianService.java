package com.shepherd.shepslibrary.service.librarian;

import com.shepherd.shepslibrary.data.dto.request.CreatePasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.CreatePasswordResponse;

public interface LibrarianService {
    CreatePasswordResponse createPassword(CreatePasswordRequest request);
}
