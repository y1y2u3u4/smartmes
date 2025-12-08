-- ============================================
-- SmartMES Lite - Initial Test Data
-- Description: 半导体生产工单跟踪系统测试数据
-- Version: 1.0
-- Created: 2025-12-08
-- ============================================

USE smartmes_lite;

-- 清空现有数据（谨慎使用，仅用于测试环境）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE audit_log;
TRUNCATE TABLE downtime_report;
TRUNCATE TABLE work_order;
TRUNCATE TABLE user;
TRUNCATE TABLE equipment;
TRUNCATE TABLE product;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 1. 插入产品数据（半导体制造相关产品）
-- ============================================
INSERT INTO product (product_code, product_name, product_type, standard_time, created_at) VALUES
('SM-001', '8-inch Silicon Wafer (200mm)', 'Wafer', 120, '2025-01-01 08:00:00'),
('SM-002', '12-inch Silicon Wafer (300mm)', 'Wafer', 180, '2025-01-01 08:00:00'),
('SM-003', 'GaN Epitaxial Wafer', 'Epitaxial', 240, '2025-01-01 08:00:00'),
('SM-004', 'SiC Power Device Wafer', 'Power Semiconductor', 300, '2025-01-02 08:00:00'),
('SM-005', 'MEMS Sensor Wafer', 'MEMS', 150, '2025-01-02 08:00:00');

-- ============================================
-- 2. 插入设备数据（半导体制造设备）
-- ============================================
INSERT INTO equipment (equipment_id, equipment_name, equipment_type, line_id, status, last_maintenance_time, next_maintenance_time, created_at) VALUES
('EQP-001', 'ASML ArF Lithography System', 'Lithography', 'LINE-01', 'RUNNING', '2025-11-15 10:00:00', '2026-02-15 10:00:00', '2024-06-01 08:00:00'),
('EQP-002', 'LAM Plasma Etcher 9600', 'Etching', 'LINE-01', 'RUNNING', '2025-11-20 14:00:00', '2026-02-20 14:00:00', '2024-06-01 08:00:00'),
('EQP-003', 'Applied Materials CVD Centura', 'CVD', 'LINE-02', 'IDLE', '2025-12-01 09:00:00', '2026-03-01 09:00:00', '2024-07-10 08:00:00'),
('EQP-004', 'Tokyo Electron Ion Implanter', 'Ion Implantation', 'LINE-02', 'FAULT', '2025-10-05 16:00:00', '2026-01-05 16:00:00', '2024-08-15 08:00:00'),
('EQP-005', 'KLA Tencor Wafer Inspection', 'Inspection', 'LINE-03', 'RUNNING', '2025-11-25 11:00:00', '2026-02-25 11:00:00', '2024-09-01 08:00:00'),
('EQP-006', 'Disco Wafer Dicing Saw', 'Dicing', 'LINE-03', 'MAINTENANCE', '2025-12-05 08:00:00', '2026-03-05 08:00:00', '2024-09-15 08:00:00'),
('EQP-007', 'Screen Semi-Auto Solder Paste Printer', 'Packaging', 'LINE-04', 'IDLE', '2025-11-10 13:00:00', '2026-02-10 13:00:00', '2024-10-01 08:00:00');

-- ============================================
-- 3. 插入用户数据（不同角色）
-- 密码统一为: password123（实际应使用BCrypt加密，这里仅为演示）
-- ============================================
INSERT INTO user (user_id, username, password, real_name, role, team, status, created_at) VALUES
('U001', 'admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Zhang Wei', 'ADMIN', 'Management', 'ACTIVE', '2024-01-01 08:00:00'),
('U002', 'supervisor_wang', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Wang Fang', 'SUPERVISOR', 'Production Team A', 'ACTIVE', '2024-01-05 08:00:00'),
('U003', 'engineer_li', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Li Ming', 'ENGINEER', 'Engineering', 'ACTIVE', '2024-01-10 08:00:00'),
('U004', 'operator_chen', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Chen Jing', 'OPERATOR', 'Production Team A', 'ACTIVE', '2024-02-01 08:00:00'),
('U005', 'operator_liu', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Liu Tao', 'OPERATOR', 'Production Team B', 'ACTIVE', '2024-02-15 08:00:00'),
('U006', 'engineer_zhao', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Zhao Qiang', 'ENGINEER', 'Engineering', 'ACTIVE', '2024-03-01 08:00:00'),
('U007', 'operator_sun', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'Sun Li', 'OPERATOR', 'Production Team B', 'INACTIVE', '2024-04-01 08:00:00');

-- ============================================
-- 4. 插入工单数据（不同状态）
-- ============================================
INSERT INTO work_order (order_id, product_code, batch_no, plan_qty, actual_qty, start_time, end_time, status, line_id, equipment_id, operator_id, created_by, created_at) VALUES
-- 已完成工单
('WO20250101001', 'SM-001', 'B20250101', 1000, 1000, '2025-01-01 08:00:00', '2025-01-01 18:00:00', 'COMPLETED', 'LINE-01', 'EQP-001', 'U004', 'U002', '2024-12-31 16:00:00'),
('WO20250102001', 'SM-002', 'B20250102', 500, 500, '2025-01-02 08:00:00', '2025-01-02 20:00:00', 'COMPLETED', 'LINE-02', 'EQP-003', 'U005', 'U002', '2025-01-01 16:00:00'),

-- 进行中工单
('WO20250103001', 'SM-001', 'B20250103', 800, 620, '2025-01-03 08:00:00', NULL, 'IN_PROGRESS', 'LINE-01', 'EQP-001', 'U004', 'U002', '2025-01-02 16:00:00'),
('WO20250103002', 'SM-003', 'B20250103A', 600, 450, '2025-01-03 09:00:00', NULL, 'IN_PROGRESS', 'LINE-02', 'EQP-003', 'U005', 'U002', '2025-01-02 17:00:00'),
('WO20250104001', 'SM-005', 'B20250104', 400, 280, '2025-01-04 08:00:00', NULL, 'IN_PROGRESS', 'LINE-03', 'EQP-005', 'U004', 'U002', '2025-01-03 15:00:00'),

-- 异常工单（因设备故障停工）
('WO20250104002', 'SM-004', 'B20250104B', 300, 85, '2025-01-04 10:00:00', NULL, 'ABNORMAL', 'LINE-02', 'EQP-004', 'U005', 'U002', '2025-01-03 16:00:00'),

-- 待开始工单
('WO20250105001', 'SM-002', 'B20250105', 1200, 0, NULL, NULL, 'PENDING', 'LINE-01', 'EQP-001', 'U004', 'U002', '2025-01-04 14:00:00'),
('WO20250105002', 'SM-001', 'B20250105A', 900, 0, NULL, NULL, 'PENDING', 'LINE-02', 'EQP-003', 'U005', 'U002', '2025-01-04 14:30:00'),
('WO20250106001', 'SM-003', 'B20250106', 700, 0, NULL, NULL, 'PENDING', 'LINE-03', 'EQP-005', 'U004', 'U002', '2025-01-04 15:00:00'),

-- 已关闭工单（完工并质检通过）
('WO20241225001', 'SM-001', 'B20241225', 1500, 1500, '2024-12-25 08:00:00', '2024-12-26 17:00:00', 'CLOSED', 'LINE-01', 'EQP-001', 'U004', 'U002', '2024-12-24 16:00:00');

-- ============================================
-- 5. 插入停机上报数据（不同类型异常）
-- ============================================
INSERT INTO downtime_report (order_id, equipment_id, downtime_type, description, start_time, end_time, duration_minutes, reporter_id, responder_id, solution, status, attachments, created_at) VALUES
-- 已解决的设备故障
('WO20250101001', 'EQP-001', 'EQUIPMENT_FAULT', 'Lithography laser power fluctuation, causing exposure quality issues', '2025-01-01 10:30:00', '2025-01-01 12:15:00', 105, 'U004', 'U003', 'Replaced laser power supply module, recalibrated system', 'RESOLVED', '["photos/20250101_eqp001_fault.jpg"]', '2025-01-01 10:32:00'),

-- 已解决的物料缺料
('WO20250102001', 'EQP-003', 'MATERIAL_SHORTAGE', 'Precursor gas (SiH4) depleted during CVD process', '2025-01-02 14:20:00', '2025-01-02 15:00:00', 40, 'U005', 'U002', 'Emergency gas cylinder replacement, production resumed', 'RESOLVED', NULL, '2025-01-02 14:22:00'),

-- 已解决的质量异常
('WO20250103001', 'EQP-001', 'QUALITY_ISSUE', 'Critical dimension (CD) out of specification range', '2025-01-03 11:00:00', '2025-01-03 12:30:00', 90, 'U004', 'U003', 'Adjusted focus offset and dose compensation parameters', 'RESOLVED', '["photos/20250103_cd_measurement.jpg", "logs/20250103_recipe_change.log"]', '2025-01-03 11:05:00'),

-- 处理中的设备故障（严重）
('WO20250104002', 'EQP-004', 'EQUIPMENT_FAULT', 'Ion implanter chamber vacuum leak detected, pressure exceeds safe threshold', '2025-01-04 11:45:00', NULL, NULL, 'U005', 'U006', 'Investigating vacuum system, suspected O-ring seal failure', 'PROCESSING', '["photos/20250104_vacuum_gauge.jpg"]', '2025-01-04 11:47:00'),

-- 待处理的物料问题
('WO20250104001', 'EQP-005', 'MATERIAL_SHORTAGE', 'Wafer carrier shortage, waiting for cleaning cycle completion', '2025-01-04 15:30:00', NULL, NULL, 'U004', NULL, NULL, 'PENDING', NULL, '2025-01-04 15:32:00'),

-- 已解决的操作失误
('WO20250103002', 'EQP-003', 'OPERATOR_ERROR', 'Incorrect recipe loaded for GaN epi growth, detected before process start', '2025-01-03 09:15:00', '2025-01-03 09:25:00', 10, 'U005', 'U002', 'Corrected recipe selection, provided operator retraining', 'RESOLVED', NULL, '2025-01-03 09:17:00'),

-- 已解决的设备故障（轻微）
('WO20250103001', 'EQP-001', 'EQUIPMENT_FAULT', 'Wafer handling robot misalignment warning', '2025-01-03 16:00:00', '2025-01-03 16:20:00', 20, 'U004', 'U003', 'Performed robot teaching and homing calibration', 'RESOLVED', NULL, '2025-01-03 16:02:00'),

-- 已解决的其他异常
(NULL, 'EQP-006', 'OTHER', 'Scheduled preventive maintenance for dicing blade replacement', '2025-12-05 08:00:00', '2025-12-05 10:30:00', 150, 'U003', 'U003', 'Completed blade replacement and spindle check', 'RESOLVED', '["maintenance/20251205_blade_replacement.pdf"]', '2025-12-05 08:05:00');

-- ============================================
-- 6. 插入审计日志示例数据
-- ============================================
INSERT INTO audit_log (user_id, username, operation, module, entity_id, details, ip_address, created_at) VALUES
('U001', 'admin', 'LOGIN', 'USER', NULL, '{"login_method": "password", "device": "Chrome/Windows"}', '192.168.1.100', '2025-01-04 08:00:00'),
('U002', 'supervisor_wang', 'CREATE', 'WORK_ORDER', 'WO20250104001', '{"product_code": "SM-005", "plan_qty": 400}', '192.168.1.105', '2025-01-03 15:01:00'),
('U004', 'operator_chen', 'UPDATE', 'WORK_ORDER', 'WO20250103001', '{"field": "actual_qty", "old_value": 580, "new_value": 620}', '192.168.1.120', '2025-01-03 14:30:00'),
('U005', 'operator_liu', 'CREATE', 'DOWNTIME', 'DR-20250104-001', '{"equipment_id": "EQP-004", "type": "EQUIPMENT_FAULT"}', '192.168.1.125', '2025-01-04 11:47:00'),
('U003', 'engineer_li', 'UPDATE', 'DOWNTIME', 'DR-20250101-001', '{"field": "status", "old_value": "PROCESSING", "new_value": "RESOLVED"}', '192.168.1.110', '2025-01-01 12:20:00'),
('U006', 'engineer_zhao', 'UPDATE', 'EQUIPMENT', 'EQP-004', '{"field": "status", "old_value": "RUNNING", "new_value": "FAULT"}', '192.168.1.112', '2025-01-04 11:50:00');

-- ============================================
-- 数据验证查询
-- ============================================

-- 统计各表数据量
SELECT 'Product Count' AS Category, COUNT(*) AS Total FROM product
UNION ALL
SELECT 'Equipment Count', COUNT(*) FROM equipment
UNION ALL
SELECT 'User Count', COUNT(*) FROM user
UNION ALL
SELECT 'Work Order Count', COUNT(*) FROM work_order
UNION ALL
SELECT 'Downtime Report Count', COUNT(*) FROM downtime_report
UNION ALL
SELECT 'Audit Log Count', COUNT(*) FROM audit_log;

-- 显示工单状态分布
SELECT
    status AS Work_Order_Status,
    COUNT(*) AS Count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM work_order), 1), '%') AS Percentage
FROM work_order
GROUP BY status
ORDER BY Count DESC;

-- 显示异常类型分布
SELECT
    downtime_type AS Downtime_Type,
    COUNT(*) AS Count,
    ROUND(AVG(duration_minutes), 1) AS Avg_Duration_Minutes
FROM downtime_report
WHERE duration_minutes IS NOT NULL
GROUP BY downtime_type
ORDER BY Count DESC;

-- ============================================
-- 初始化完成
-- ============================================
SELECT '✓ Test data initialization completed successfully!' AS STATUS;
SELECT '✓ Total 5 products, 7 equipment, 7 users, 10 work orders, 8 downtime reports' AS SUMMARY;
