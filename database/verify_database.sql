-- ============================================
-- SmartMES Lite - Database Verification Script
-- Description: 验证数据库安装和数据完整性
-- Version: 1.0
-- Created: 2025-12-08
-- Usage: mysql -u root -p < verify_database.sql
-- ============================================

USE smartmes_lite;

SELECT '==================================================' AS '';
SELECT '  SmartMES Lite Database Verification Report' AS '';
SELECT '==================================================' AS '';
SELECT '' AS '';

-- 检查1: 表结构验证
SELECT '>>> Check 1: Table Structure Verification' AS '';
SELECT '-----------------------------------' AS '';

SELECT
    table_name AS Table_Name,
    engine AS Engine,
    table_rows AS Approximate_Rows,
    ROUND((data_length + index_length) / 1024 / 1024, 2) AS Size_MB,
    table_collation AS Collation
FROM information_schema.tables
WHERE table_schema = 'smartmes_lite'
ORDER BY table_name;

SELECT '' AS '';

-- 检查2: 索引验证
SELECT '>>> Check 2: Index Verification' AS '';
SELECT '-----------------------------------' AS '';

SELECT
    table_name AS Table_Name,
    index_name AS Index_Name,
    GROUP_CONCAT(column_name ORDER BY seq_in_index) AS Indexed_Columns,
    index_type AS Index_Type,
    CASE non_unique
        WHEN 0 THEN 'UNIQUE'
        ELSE 'NON-UNIQUE'
    END AS Uniqueness
FROM information_schema.statistics
WHERE table_schema = 'smartmes_lite'
    AND index_name != 'PRIMARY'
GROUP BY table_name, index_name, index_type, non_unique
ORDER BY table_name, index_name;

SELECT '' AS '';

-- 检查3: 外键验证
SELECT '>>> Check 3: Foreign Key Constraints' AS '';
SELECT '-----------------------------------' AS '';

SELECT
    constraint_name AS FK_Name,
    table_name AS From_Table,
    column_name AS From_Column,
    referenced_table_name AS To_Table,
    referenced_column_name AS To_Column
FROM information_schema.key_column_usage
WHERE table_schema = 'smartmes_lite'
    AND referenced_table_name IS NOT NULL
ORDER BY table_name, constraint_name;

SELECT '' AS '';

-- 检查4: 数据完整性验证
SELECT '>>> Check 4: Data Integrity Verification' AS '';
SELECT '-----------------------------------' AS '';

-- 4.1 表记录数统计
SELECT 'Table Row Counts:' AS '';
SELECT table_name AS Table_Name, COUNT(*) AS Row_Count
FROM (
    SELECT 'product' AS table_name FROM product
    UNION ALL SELECT 'equipment' FROM equipment
    UNION ALL SELECT 'user' FROM user
    UNION ALL SELECT 'work_order' FROM work_order
    UNION ALL SELECT 'downtime_report' FROM downtime_report
    UNION ALL SELECT 'audit_log' FROM audit_log
) t
GROUP BY table_name
ORDER BY table_name;

SELECT '' AS '';

-- 4.2 工单状态分布
SELECT 'Work Order Status Distribution:' AS '';
SELECT
    status AS Status,
    COUNT(*) AS Count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM work_order), 1), '%') AS Percentage
FROM work_order
GROUP BY status
ORDER BY Count DESC;

SELECT '' AS '';

-- 4.3 异常类型分布
SELECT 'Downtime Type Distribution:' AS '';
SELECT
    downtime_type AS Downtime_Type,
    COUNT(*) AS Count,
    CASE
        WHEN SUM(CASE WHEN duration_minutes IS NOT NULL THEN 1 ELSE 0 END) > 0
        THEN ROUND(AVG(duration_minutes), 1)
        ELSE 0
    END AS Avg_Duration_Minutes
FROM downtime_report
GROUP BY downtime_type
ORDER BY Count DESC;

SELECT '' AS '';

-- 4.4 用户角色分布
SELECT 'User Role Distribution:' AS '';
SELECT
    role AS Role,
    COUNT(*) AS Count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM user), 1), '%') AS Percentage
FROM user
GROUP BY role
ORDER BY Count DESC;

SELECT '' AS '';

-- 检查5: 引用完整性检查
SELECT '>>> Check 5: Referential Integrity Check' AS '';
SELECT '-----------------------------------' AS '';

-- 5.1 检查工单是否引用了不存在的产品
SELECT 'Orphaned Product References in Work Orders:' AS '';
SELECT COUNT(*) AS Orphaned_Count
FROM work_order wo
LEFT JOIN product p ON wo.product_code = p.product_code
WHERE p.product_code IS NULL;

-- 5.2 检查工单是否引用了不存在的设备
SELECT 'Orphaned Equipment References in Work Orders:' AS '';
SELECT COUNT(*) AS Orphaned_Count
FROM work_order wo
LEFT JOIN equipment e ON wo.equipment_id = e.equipment_id
WHERE wo.equipment_id IS NOT NULL AND e.equipment_id IS NULL;

-- 5.3 检查停机报告是否引用了不存在的设备
SELECT 'Orphaned Equipment References in Downtime Reports:' AS '';
SELECT COUNT(*) AS Orphaned_Count
FROM downtime_report dr
LEFT JOIN equipment e ON dr.equipment_id = e.equipment_id
WHERE e.equipment_id IS NULL;

-- 5.4 检查停机报告是否引用了不存在的工单
SELECT 'Orphaned Work Order References in Downtime Reports:' AS '';
SELECT COUNT(*) AS Orphaned_Count
FROM downtime_report dr
LEFT JOIN work_order wo ON dr.order_id = wo.order_id
WHERE dr.order_id IS NOT NULL AND wo.order_id IS NULL;

SELECT '' AS '';

-- 检查6: 数据质量检查
SELECT '>>> Check 6: Data Quality Check' AS '';
SELECT '-----------------------------------' AS '';

-- 6.1 检查空值或无效数据
SELECT 'Work Orders with Invalid Data:' AS '';
SELECT
    'Missing product_code' AS Issue,
    COUNT(*) AS Count
FROM work_order
WHERE product_code IS NULL OR product_code = ''
UNION ALL
SELECT
    'Negative plan_qty',
    COUNT(*)
FROM work_order
WHERE plan_qty < 0
UNION ALL
SELECT
    'actual_qty > plan_qty',
    COUNT(*)
FROM work_order
WHERE actual_qty > plan_qty
UNION ALL
SELECT
    'end_time < start_time',
    COUNT(*)
FROM work_order
WHERE end_time IS NOT NULL AND start_time IS NOT NULL AND end_time < start_time;

SELECT '' AS '';

SELECT 'Downtime Reports with Invalid Data:' AS '';
SELECT
    'Missing equipment_id' AS Issue,
    COUNT(*) AS Count
FROM downtime_report
WHERE equipment_id IS NULL OR equipment_id = ''
UNION ALL
SELECT
    'Missing downtime_type',
    COUNT(*)
FROM downtime_report
WHERE downtime_type IS NULL OR downtime_type = ''
UNION ALL
SELECT
    'end_time < start_time',
    COUNT(*)
FROM downtime_report
WHERE end_time IS NOT NULL AND start_time IS NOT NULL AND end_time < start_time
UNION ALL
SELECT
    'Negative duration',
    COUNT(*)
FROM downtime_report
WHERE duration_minutes < 0;

SELECT '' AS '';

-- 检查7: 触发器验证
SELECT '>>> Check 7: Trigger Verification' AS '';
SELECT '-----------------------------------' AS '';

SELECT
    trigger_name AS Trigger_Name,
    event_manipulation AS Event,
    event_object_table AS Table_Name,
    action_timing AS Timing,
    action_statement AS Action
FROM information_schema.triggers
WHERE trigger_schema = 'smartmes_lite'
ORDER BY event_object_table, trigger_name;

SELECT '' AS '';

-- 检查8: 视图验证（如果存在）
SELECT '>>> Check 8: View Verification' AS '';
SELECT '-----------------------------------' AS '';

SELECT
    table_name AS View_Name,
    view_definition AS Definition
FROM information_schema.views
WHERE table_schema = 'smartmes_lite'
ORDER BY table_name;

-- 如果没有视图，显示提示信息
SELECT
    CASE
        WHEN (SELECT COUNT(*) FROM information_schema.views WHERE table_schema = 'smartmes_lite') = 0
        THEN 'No views found. Run views_and_queries.sql to create useful views.'
        ELSE 'Views verified successfully.'
    END AS Status;

SELECT '' AS '';

-- 检查9: 存储过程验证（如果存在）
SELECT '>>> Check 9: Stored Procedures Verification' AS '';
SELECT '-----------------------------------' AS '';

SELECT
    routine_name AS Procedure_Name,
    routine_type AS Type,
    data_type AS Return_Type,
    routine_definition AS Definition
FROM information_schema.routines
WHERE routine_schema = 'smartmes_lite'
ORDER BY routine_name;

-- 如果没有存储过程，显示提示信息
SELECT
    CASE
        WHEN (SELECT COUNT(*) FROM information_schema.routines WHERE routine_schema = 'smartmes_lite') = 0
        THEN 'No stored procedures found. Run views_and_queries.sql to create useful procedures.'
        ELSE 'Stored procedures verified successfully.'
    END AS Status;

SELECT '' AS '';

-- 检查10: 字符集和排序规则验证
SELECT '>>> Check 10: Character Set and Collation Check' AS '';
SELECT '-----------------------------------' AS '';

SELECT
    table_name AS Table_Name,
    table_collation AS Collation,
    CASE
        WHEN table_collation = 'utf8mb4_unicode_ci' THEN '✓ OK'
        ELSE '✗ NOT utf8mb4_unicode_ci'
    END AS Status
FROM information_schema.tables
WHERE table_schema = 'smartmes_lite'
ORDER BY table_name;

SELECT '' AS '';

-- 最终总结
SELECT '==================================================' AS '';
SELECT '  Verification Summary' AS '';
SELECT '==================================================' AS '';

SELECT
    'Database Name' AS Item,
    DATABASE() AS Value
UNION ALL
SELECT
    'Total Tables',
    CAST(COUNT(DISTINCT table_name) AS CHAR)
FROM information_schema.tables
WHERE table_schema = 'smartmes_lite'
UNION ALL
SELECT
    'Total Indexes',
    CAST(COUNT(DISTINCT index_name) AS CHAR)
FROM information_schema.statistics
WHERE table_schema = 'smartmes_lite'
UNION ALL
SELECT
    'Total Foreign Keys',
    CAST(COUNT(DISTINCT constraint_name) AS CHAR)
FROM information_schema.key_column_usage
WHERE table_schema = 'smartmes_lite'
    AND referenced_table_name IS NOT NULL
UNION ALL
SELECT
    'Total Triggers',
    CAST(COUNT(*) AS CHAR)
FROM information_schema.triggers
WHERE trigger_schema = 'smartmes_lite'
UNION ALL
SELECT
    'Total Views',
    CAST(COUNT(*) AS CHAR)
FROM information_schema.views
WHERE table_schema = 'smartmes_lite'
UNION ALL
SELECT
    'Total Records (all tables)',
    CAST(
        (SELECT COUNT(*) FROM product) +
        (SELECT COUNT(*) FROM equipment) +
        (SELECT COUNT(*) FROM user) +
        (SELECT COUNT(*) FROM work_order) +
        (SELECT COUNT(*) FROM downtime_report) +
        (SELECT COUNT(*) FROM audit_log)
    AS CHAR);

SELECT '' AS '';
SELECT '==================================================' AS '';
SELECT '  Verification Complete!' AS '';
SELECT '==================================================' AS '';

-- 显示任何警告或建议
SELECT '' AS '';
SELECT 'Recommendations:' AS '';

SELECT
    CASE
        WHEN (SELECT COUNT(*) FROM information_schema.views WHERE table_schema = 'smartmes_lite') = 0
        THEN '• Run views_and_queries.sql to create useful database views'
        ELSE '• Database views are available'
    END AS Recommendation
UNION ALL
SELECT
    CASE
        WHEN (SELECT COUNT(*) FROM work_order WHERE actual_qty > plan_qty) > 0
        THEN CONCAT('• WARNING: ', (SELECT COUNT(*) FROM work_order WHERE actual_qty > plan_qty), ' work orders have actual_qty > plan_qty')
        ELSE '• All work order quantities are valid'
    END
UNION ALL
SELECT
    CASE
        WHEN (SELECT COUNT(*) FROM downtime_report WHERE status = 'PENDING') > 0
        THEN CONCAT('• ', (SELECT COUNT(*) FROM downtime_report WHERE status = 'PENDING'), ' pending downtime reports need attention')
        ELSE '• No pending downtime reports'
    END
UNION ALL
SELECT
    CASE
        WHEN (SELECT COUNT(*) FROM equipment WHERE status = 'FAULT') > 0
        THEN CONCAT('• WARNING: ', (SELECT COUNT(*) FROM equipment WHERE status = 'FAULT'), ' equipment in FAULT status')
        ELSE '• All equipment status is normal'
    END;

SELECT '' AS '';
