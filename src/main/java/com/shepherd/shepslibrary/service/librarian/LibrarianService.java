package com.shepherd.shepslibrary.service.librarian;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.CreatePasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.AuthResponse;

public interface LibrarianService {
    BaseResponse<AuthResponse> createPassword(CreatePasswordRequest request);
}
