package com.shepherd.shepslibrary.service.admin;

import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.data.dto.response.InviteLibrarianResponse;


public interface AdminService {
    InviteLibrarianResponse inviteLibrarian(InviteLibrarianRequest request);
}
