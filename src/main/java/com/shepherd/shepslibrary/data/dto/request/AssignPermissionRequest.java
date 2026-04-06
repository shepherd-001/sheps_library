package com.shepherd.shepslibrary.data.dto.request;

import java.util.List;

public record AssignPermissionRequest(
        String roleName,

        List<String> permissionNames
){}
