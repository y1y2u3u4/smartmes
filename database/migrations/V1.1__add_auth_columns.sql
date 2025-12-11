-- ============================================
-- SmartMES Lite - Database Migration V1.1
-- Description: Add authentication columns to user table
-- Version: 1.1
-- ============================================

-- Add authentication columns to user table
ALTER TABLE user ADD COLUMN last_login_time DATETIME COMMENT 'Last login time';
ALTER TABLE user ADD COLUMN login_attempts INT DEFAULT 0 COMMENT 'Failed login attempts count';
ALTER TABLE user ADD COLUMN locked_until DATETIME COMMENT 'Account locked until this time';

-- Create token blacklist table for logout functionality
CREATE TABLE IF NOT EXISTS token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Record ID',
    token_hash VARCHAR(64) NOT NULL COMMENT 'SHA-256 hash of the token',
    expires_at DATETIME NOT NULL COMMENT 'Token expiration time',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation time',
    INDEX idx_token_hash (token_hash) COMMENT 'Token hash index for fast lookup',
    INDEX idx_expires_at (expires_at) COMMENT 'Expiration index for cleanup'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Token blacklist for invalidated tokens';

-- Insert default admin user (password: Admin123)
-- Password is BCrypt encoded with strength 10
INSERT INTO user (user_id, username, password, real_name, role, team, status, created_at, updated_at)
VALUES (
    'U001',
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
    'System Admin',
    'ADMIN',
    'IT Department',
    'ACTIVE',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Insert test users
INSERT INTO user (user_id, username, password, real_name, role, team, status, created_at, updated_at)
VALUES
    ('U002', 'operator1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'John Operator', 'OPERATOR', 'Production Line A', 'ACTIVE', NOW(), NOW()),
    ('U003', 'supervisor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Jane Supervisor', 'SUPERVISOR', 'Production Line A', 'ACTIVE', NOW(), NOW()),
    ('U004', 'engineer1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Bob Engineer', 'ENGINEER', 'Engineering', 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Create scheduled event to clean up expired blacklisted tokens (runs daily)
-- Note: Requires event_scheduler to be enabled in MySQL
-- SET GLOBAL event_scheduler = ON;
CREATE EVENT IF NOT EXISTS cleanup_expired_tokens
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
    DELETE FROM token_blacklist WHERE expires_at < NOW();

SELECT 'Migration V1.1 completed successfully!' AS STATUS;
