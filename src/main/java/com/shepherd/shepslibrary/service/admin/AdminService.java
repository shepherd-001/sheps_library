package com.shepherd.shepslibrary.service.admin;

import com.shepherd.shepslibrary.data.dto.response.InviteLibrarianResponse;

import java.util.Set;

public interface AdminService {
    InviteLibrarianResponse inviteLibrarian(Set<String> librarianEmails);
}
