package com.smartmes.service;

/**
 * Audit log service interface
 */
public interface AuditLogService {

    /**
     * Log user login attempt
     */
    void logLogin(String userId, String username, String ipAddress, boolean success);

    /**
     * Log user logout
     */
    void logLogout(String userId, String username);

    /**
     * Log password change
     */
    void logPasswordChange(String userId, String username);

    /**
     * Log generic operation
     */
    void logOperation(String userId, String username, String operation, String module, String entityId, String details, String ipAddress);
}
