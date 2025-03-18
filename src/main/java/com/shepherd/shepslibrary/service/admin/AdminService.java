package com.shepherd.shepslibrary.service.admin;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.data.dto.response.InviteLibrarianResponse;


public interface AdminService {
    BaseResponse<InviteLibrarianResponse> inviteLibrarian(InviteLibrarianRequest request);
    BaseResponse<String> resendInvite(String inviteeEmail);
}
