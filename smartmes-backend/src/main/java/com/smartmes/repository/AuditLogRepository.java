package com.smartmes.repository;

import com.smartmes.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Audit log repository
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find logs by user ID
     */
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * Find logs by operation type
     */
    List<AuditLog> findByOperationOrderByCreatedAtDesc(String operation);

    /**
     * Find logs by module
     */
    List<AuditLog> findByModuleOrderByCreatedAtDesc(String module);

    /**
     * Find logs within time range
     */
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
}
