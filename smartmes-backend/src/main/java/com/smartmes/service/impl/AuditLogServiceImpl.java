package com.smartmes.service.impl;

import com.smartmes.entity.AuditLog;
import com.smartmes.repository.AuditLogRepository;
import com.smartmes.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Audit log service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    public void logLogin(String userId, String username, String ipAddress, boolean success) {
        String details = success ? "Login successful" : "Login failed";
        logOperation(userId, username, "LOGIN", "USER", userId, details, ipAddress);
    }

    @Override
    @Async
    public void logLogout(String userId, String username) {
        logOperation(userId, username, "LOGOUT", "USER", userId, "User logged out", null);
    }

    @Override
    @Async
    public void logPasswordChange(String userId, String username) {
        logOperation(userId, username, "UPDATE", "USER", userId, "Password changed", null);
    }

    @Override
    public void logOperation(String userId, String username, String operation, String module, String entityId, String details, String ipAddress) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setUsername(username);
            auditLog.setOperation(operation);
            auditLog.setModule(module);
            auditLog.setEntityId(entityId);
            auditLog.setDetails(details);
            auditLog.setIpAddress(ipAddress);

            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} - {} - {}", operation, module, entityId);
        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }
}
