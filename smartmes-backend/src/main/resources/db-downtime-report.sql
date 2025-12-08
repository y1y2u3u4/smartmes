-- ============================================
-- SmartMES Lite - Downtime Report Table
-- 异常停机上报表
-- ============================================

CREATE TABLE IF NOT EXISTS downtime_report (
    -- Primary Key
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Report ID (auto-increment)',

    -- Basic Information
    order_id VARCHAR(50) NOT NULL COMMENT 'Associated order ID',
    equipment_id VARCHAR(50) NOT NULL COMMENT 'Equipment ID',
    downtime_type VARCHAR(30) NOT NULL COMMENT 'Downtime type: EQUIPMENT_FAILURE, MATERIAL_SHORTAGE, QUALITY_ISSUE, OPERATOR_ERROR, OTHER',
    description TEXT NOT NULL COMMENT 'Description of the issue',

    -- Time Information
    start_time DATETIME NOT NULL COMMENT 'Downtime start time',
    end_time DATETIME DEFAULT NULL COMMENT 'Downtime end time',
    duration_minutes INT DEFAULT NULL COMMENT 'Downtime duration in minutes (auto-calculated)',

    -- Personnel Information
    reporter_id VARCHAR(50) NOT NULL COMMENT 'Reporter ID',
    responder_id VARCHAR(50) DEFAULT NULL COMMENT 'Responder ID',

    -- Resolution Information
    solution TEXT DEFAULT NULL COMMENT 'Solution description',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Status: PENDING, PROCESSING, RESOLVED',

    -- Attachments
    attachments TEXT DEFAULT NULL COMMENT 'Attachment file paths (comma-separated)',

    -- Audit Fields
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',

    -- Indexes
    INDEX idx_order_id (order_id) COMMENT 'Order ID index',
    INDEX idx_equipment_id (equipment_id) COMMENT 'Equipment ID index',
    INDEX idx_downtime_type (downtime_type) COMMENT 'Downtime type index',
    INDEX idx_status (status) COMMENT 'Status index',
    INDEX idx_start_time (start_time) COMMENT 'Start time index',
    INDEX idx_reporter_id (reporter_id) COMMENT 'Reporter ID index',
    INDEX idx_created_at (created_at) COMMENT 'Creation time index'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Downtime Report Table - Records production downtime incidents';

-- ============================================
-- Sample Data (Optional)
-- ============================================

-- INSERT INTO downtime_report (
--     order_id, equipment_id, downtime_type, description,
--     start_time, end_time, duration_minutes,
--     reporter_id, responder_id, solution, status
-- ) VALUES
-- ('WO-20240101-001', 'EQ-001', 'EQUIPMENT_FAILURE', 'Conveyor belt motor malfunction',
--  '2024-01-15 09:30:00', '2024-01-15 11:00:00', 90,
--  'USER-001', 'TECH-001', 'Replaced faulty motor', 'RESOLVED'),
--
-- ('WO-20240101-002', 'EQ-002', 'MATERIAL_SHORTAGE', 'Insufficient raw material for production',
--  '2024-01-15 14:00:00', '2024-01-15 14:45:00', 45,
--  'USER-002', 'SUPERVISOR-001', 'Emergency material delivery arranged', 'RESOLVED'),
--
-- ('WO-20240101-003', 'EQ-001', 'QUALITY_ISSUE', 'Product quality below standard',
--  '2024-01-16 10:00:00', NULL, NULL,
--  'USER-001', 'QC-001', NULL, 'PROCESSING');
