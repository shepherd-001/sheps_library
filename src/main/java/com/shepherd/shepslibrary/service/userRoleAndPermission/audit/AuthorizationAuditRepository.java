package com.shepherd.shepslibrary.service.userRoleAndPermission.audit;

import org.springframework.data.repository.Repository;

/** No application update/delete API for committed audit records. */
public interface AuthorizationAuditRepository extends Repository<AuthorizationAudit, Long> {
    AuthorizationAudit save(AuthorizationAudit event);
}
