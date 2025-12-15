package com.shepherd.shepslibrary.auditing;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
    @Transactional
    @Modifying
    @Query("delete from AuditLog a where a.timeStamp < :cutoff")
    long deleteByTimeStampBefore(@Param("cutoff") Instant cutoff);
}
