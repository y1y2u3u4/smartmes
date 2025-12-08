-- ============================================
-- SmartMES Lite - Database Views & Common Queries
-- Description: 常用数据库视图和查询语句
-- Version: 1.0
-- Created: 2025-12-08
-- ============================================

USE smartmes_lite;

-- ============================================
-- 视图定义 (Database Views)
-- ============================================

-- 视图1: 工单详细信息视图 (Work Order Detail View)
-- 用途: 联合查询工单及关联的产品、设备、操作员信息
DROP VIEW IF EXISTS v_work_order_detail;
CREATE VIEW v_work_order_detail AS
SELECT
    wo.order_id,
    wo.batch_no,
    p.product_code,
    p.product_name,
    p.product_type,
    wo.plan_qty,
    wo.actual_qty,
    CASE
        WHEN wo.plan_qty > 0 THEN ROUND((wo.actual_qty * 100.0 / wo.plan_qty), 1)
        ELSE 0
    END AS completion_rate,
    wo.start_time,
    wo.end_time,
    CASE
        WHEN wo.end_time IS NOT NULL THEN TIMESTAMPDIFF(MINUTE, wo.start_time, wo.end_time)
        WHEN wo.start_time IS NOT NULL THEN TIMESTAMPDIFF(MINUTE, wo.start_time, NOW())
        ELSE NULL
    END AS duration_minutes,
    wo.status,
    wo.line_id,
    e.equipment_id,
    e.equipment_name,
    e.equipment_type,
    u.user_id AS operator_id,
    u.real_name AS operator_name,
    u.team AS operator_team,
    wo.created_by,
    wo.created_at,
    wo.updated_at
FROM work_order wo
JOIN product p ON wo.product_code = p.product_code
LEFT JOIN equipment e ON wo.equipment_id = e.equipment_id
LEFT JOIN user u ON wo.operator_id = u.user_id;

-- 视图2: 停机报告详细视图 (Downtime Report Detail View)
-- 用途: 联合查询停机记录及相关设备、人员信息
DROP VIEW IF EXISTS v_downtime_detail;
CREATE VIEW v_downtime_detail AS
SELECT
    dr.report_id,
    dr.order_id,
    wo.batch_no,
    e.equipment_id,
    e.equipment_name,
    e.equipment_type,
    e.line_id,
    dr.downtime_type,
    dr.description,
    dr.start_time,
    dr.end_time,
    dr.duration_minutes,
    CASE
        WHEN dr.end_time IS NULL AND dr.status != 'RESOLVED' THEN TIMESTAMPDIFF(MINUTE, dr.start_time, NOW())
        ELSE dr.duration_minutes
    END AS current_duration_minutes,
    dr.status,
    u1.user_id AS reporter_id,
    u1.real_name AS reporter_name,
    u2.user_id AS responder_id,
    u2.real_name AS responder_name,
    dr.solution,
    dr.attachments,
    dr.created_at,
    dr.updated_at
FROM downtime_report dr
JOIN equipment e ON dr.equipment_id = e.equipment_id
LEFT JOIN work_order wo ON dr.order_id = wo.order_id
LEFT JOIN user u1 ON dr.reporter_id = u1.user_id
LEFT JOIN user u2 ON dr.responder_id = u2.user_id;

-- 视图3: 设备状态概览视图 (Equipment Status Overview)
-- 用途: 快速查看设备当前状态和维护信息
DROP VIEW IF EXISTS v_equipment_status;
CREATE VIEW v_equipment_status AS
SELECT
    e.equipment_id,
    e.equipment_name,
    e.equipment_type,
    e.line_id,
    e.status,
    e.last_maintenance_time,
    e.next_maintenance_time,
    DATEDIFF(e.next_maintenance_time, NOW()) AS days_until_maintenance,
    CASE
        WHEN DATEDIFF(e.next_maintenance_time, NOW()) < 0 THEN 'OVERDUE'
        WHEN DATEDIFF(e.next_maintenance_time, NOW()) <= 7 THEN 'URGENT'
        WHEN DATEDIFF(e.next_maintenance_time, NOW()) <= 30 THEN 'UPCOMING'
        ELSE 'NORMAL'
    END AS maintenance_alert,
    (SELECT COUNT(*) FROM downtime_report WHERE equipment_id = e.equipment_id AND status != 'RESOLVED') AS active_issues,
    (SELECT COUNT(*) FROM downtime_report WHERE equipment_id = e.equipment_id AND DATE(created_at) = CURDATE()) AS today_issues,
    (SELECT COUNT(*) FROM work_order WHERE equipment_id = e.equipment_id AND status = 'IN_PROGRESS') AS active_orders
FROM equipment e;

-- 视图4: 生产统计日报视图 (Daily Production Statistics)
-- 用途: 生成每日生产统计数据
DROP VIEW IF EXISTS v_daily_production_stats;
CREATE VIEW v_daily_production_stats AS
SELECT
    DATE(wo.created_at) AS production_date,
    COUNT(DISTINCT wo.order_id) AS total_orders,
    SUM(CASE WHEN wo.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_orders,
    SUM(CASE WHEN wo.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress_orders,
    SUM(CASE WHEN wo.status = 'ABNORMAL' THEN 1 ELSE 0 END) AS abnormal_orders,
    SUM(wo.plan_qty) AS total_plan_qty,
    SUM(wo.actual_qty) AS total_actual_qty,
    CASE
        WHEN SUM(wo.plan_qty) > 0 THEN ROUND((SUM(wo.actual_qty) * 100.0 / SUM(wo.plan_qty)), 1)
        ELSE 0
    END AS overall_completion_rate,
    COUNT(DISTINCT wo.equipment_id) AS equipment_used
FROM work_order wo
GROUP BY DATE(wo.created_at);

-- ============================================
-- 常用查询 (Common Queries)
-- ============================================

-- 查询1: 当前进行中的工单列表（按优先级排序）
-- Usage: 用于生产看板显示
SELECT
    order_id,
    product_name,
    batch_no,
    plan_qty,
    actual_qty,
    completion_rate,
    equipment_name,
    operator_name,
    duration_minutes,
    CASE
        WHEN completion_rate >= 90 THEN 'LOW'
        WHEN completion_rate >= 70 THEN 'MEDIUM'
        ELSE 'HIGH'
    END AS priority
FROM v_work_order_detail
WHERE status = 'IN_PROGRESS'
ORDER BY completion_rate ASC, start_time ASC;

-- 查询2: 待处理的异常停机列表（按紧急程度排序）
-- Usage: 异常管理看板
SELECT
    report_id,
    order_id,
    equipment_name,
    downtime_type,
    description,
    start_time,
    current_duration_minutes,
    reporter_name,
    responder_name,
    CASE
        WHEN current_duration_minutes > 120 THEN 'CRITICAL'
        WHEN current_duration_minutes > 60 THEN 'HIGH'
        WHEN current_duration_minutes > 30 THEN 'MEDIUM'
        ELSE 'LOW'
    END AS urgency_level
FROM v_downtime_detail
WHERE status IN ('PENDING', 'PROCESSING')
ORDER BY current_duration_minutes DESC;

-- 查询3: 设备OEE计算（简化版）
-- OEE = Availability × Performance × Quality
-- 注: 这是简化版本，实际OEE计算需要更详细的设备运行数据
SELECT
    e.equipment_id,
    e.equipment_name,
    -- Availability: (计划时间 - 停机时间) / 计划时间
    ROUND(
        (1 - COALESCE(SUM(dr.duration_minutes), 0) / (8 * 60)) * 100,
        1
    ) AS availability_percent,
    -- Performance: 实际产量 / 理论产量（基于标准工时）
    ROUND(
        CASE
            WHEN SUM(wo.plan_qty) > 0 THEN (SUM(wo.actual_qty) * 100.0 / SUM(wo.plan_qty))
            ELSE 0
        END,
        1
    ) AS performance_percent,
    -- Overall Equipment Effectiveness (简化计算)
    ROUND(
        (1 - COALESCE(SUM(dr.duration_minutes), 0) / (8 * 60)) *
        CASE
            WHEN SUM(wo.plan_qty) > 0 THEN (SUM(wo.actual_qty) / SUM(wo.plan_qty))
            ELSE 0
        END * 100,
        1
    ) AS simplified_oee_percent
FROM equipment e
LEFT JOIN downtime_report dr ON e.equipment_id = dr.equipment_id
    AND DATE(dr.start_time) = CURDATE()
    AND dr.duration_minutes IS NOT NULL
LEFT JOIN work_order wo ON e.equipment_id = wo.equipment_id
    AND DATE(wo.created_at) = CURDATE()
GROUP BY e.equipment_id, e.equipment_name;

-- 查询4: 异常类型帕累托分析（当月）
-- Usage: 识别最频繁的异常类型
SELECT
    downtime_type,
    COUNT(*) AS occurrence_count,
    SUM(duration_minutes) AS total_duration_minutes,
    ROUND(AVG(duration_minutes), 1) AS avg_duration_minutes,
    ROUND(
        COUNT(*) * 100.0 / (SELECT COUNT(*) FROM downtime_report WHERE MONTH(created_at) = MONTH(CURDATE())),
        1
    ) AS percentage_of_total,
    ROUND(
        SUM(COUNT(*)) OVER (ORDER BY COUNT(*) DESC) * 100.0 / (SELECT COUNT(*) FROM downtime_report WHERE MONTH(created_at) = MONTH(CURDATE())),
        1
    ) AS cumulative_percentage
FROM downtime_report
WHERE MONTH(created_at) = MONTH(CURDATE())
    AND YEAR(created_at) = YEAR(CURDATE())
GROUP BY downtime_type
ORDER BY occurrence_count DESC;

-- 查询5: 批次追溯查询（根据批次号查询完整生产记录）
-- Usage: 质量追溯，输入批次号查询所有相关记录
-- Example: SET @batch_no = 'B20250103';
SET @batch_no = 'B20250103';

SELECT
    'Work Order' AS record_type,
    wo.order_id AS record_id,
    wo.batch_no,
    p.product_name AS description,
    e.equipment_name AS equipment,
    u.real_name AS operator,
    wo.start_time AS timestamp,
    wo.status AS status
FROM work_order wo
JOIN product p ON wo.product_code = p.product_code
LEFT JOIN equipment e ON wo.equipment_id = e.equipment_id
LEFT JOIN user u ON wo.operator_id = u.user_id
WHERE wo.batch_no = @batch_no

UNION ALL

SELECT
    'Downtime Event' AS record_type,
    CAST(dr.report_id AS CHAR) AS record_id,
    wo.batch_no,
    dr.description AS description,
    e.equipment_name AS equipment,
    u.real_name AS operator,
    dr.start_time AS timestamp,
    dr.status AS status
FROM downtime_report dr
JOIN work_order wo ON dr.order_id = wo.order_id
JOIN equipment e ON dr.equipment_id = e.equipment_id
LEFT JOIN user u ON dr.reporter_id = u.user_id
WHERE wo.batch_no = @batch_no

ORDER BY timestamp ASC;

-- 查询6: 操作员绩效统计（当月）
-- Usage: 评估操作员工作效率
SELECT
    u.user_id,
    u.real_name AS operator_name,
    u.team,
    COUNT(DISTINCT wo.order_id) AS orders_handled,
    SUM(wo.actual_qty) AS total_output,
    ROUND(AVG(
        CASE
            WHEN wo.plan_qty > 0 THEN (wo.actual_qty * 100.0 / wo.plan_qty)
            ELSE 0
        END
    ), 1) AS avg_completion_rate,
    COUNT(DISTINCT CASE WHEN wo.status = 'ABNORMAL' THEN wo.order_id END) AS abnormal_orders,
    ROUND(
        COUNT(DISTINCT CASE WHEN wo.status = 'ABNORMAL' THEN wo.order_id END) * 100.0 / COUNT(DISTINCT wo.order_id),
        1
    ) AS abnormal_rate
FROM user u
LEFT JOIN work_order wo ON u.user_id = wo.operator_id
    AND MONTH(wo.created_at) = MONTH(CURDATE())
    AND YEAR(wo.created_at) = YEAR(CURDATE())
WHERE u.role = 'OPERATOR'
GROUP BY u.user_id, u.real_name, u.team
HAVING COUNT(DISTINCT wo.order_id) > 0
ORDER BY avg_completion_rate DESC;

-- 查询7: 产线产能分析（本周）
-- Usage: 评估各产线生产能力
SELECT
    wo.line_id,
    COUNT(DISTINCT wo.order_id) AS total_orders,
    COUNT(DISTINCT wo.equipment_id) AS equipment_count,
    SUM(wo.plan_qty) AS planned_quantity,
    SUM(wo.actual_qty) AS actual_quantity,
    ROUND((SUM(wo.actual_qty) * 100.0 / SUM(wo.plan_qty)), 1) AS achievement_rate,
    SUM(TIMESTAMPDIFF(MINUTE, wo.start_time, COALESCE(wo.end_time, NOW()))) AS total_production_minutes,
    COALESCE(SUM(dr.duration_minutes), 0) AS total_downtime_minutes,
    ROUND(
        COALESCE(SUM(dr.duration_minutes), 0) * 100.0 /
        SUM(TIMESTAMPDIFF(MINUTE, wo.start_time, COALESCE(wo.end_time, NOW()))),
        1
    ) AS downtime_rate
FROM work_order wo
LEFT JOIN downtime_report dr ON wo.order_id = dr.order_id
WHERE YEARWEEK(wo.created_at, 1) = YEARWEEK(CURDATE(), 1)
GROUP BY wo.line_id
ORDER BY achievement_rate DESC;

-- 查询8: 即将到期维护的设备列表
-- Usage: 预防性维护计划
SELECT
    equipment_id,
    equipment_name,
    equipment_type,
    line_id,
    last_maintenance_time,
    next_maintenance_time,
    days_until_maintenance,
    maintenance_alert,
    CASE
        WHEN maintenance_alert = 'OVERDUE' THEN '⚠️ OVERDUE - Immediate Action Required'
        WHEN maintenance_alert = 'URGENT' THEN '🔴 URGENT - Within 7 days'
        WHEN maintenance_alert = 'UPCOMING' THEN '🟡 UPCOMING - Within 30 days'
        ELSE '🟢 NORMAL'
    END AS alert_message
FROM v_equipment_status
WHERE maintenance_alert IN ('OVERDUE', 'URGENT', 'UPCOMING')
ORDER BY
    CASE maintenance_alert
        WHEN 'OVERDUE' THEN 1
        WHEN 'URGENT' THEN 2
        WHEN 'UPCOMING' THEN 3
    END,
    next_maintenance_time ASC;

-- ============================================
-- 数据分析存储过程 (Stored Procedures)
-- ============================================

-- 存储过程1: 生成设备停机时间报告
DELIMITER $$

DROP PROCEDURE IF EXISTS sp_equipment_downtime_report$$
CREATE PROCEDURE sp_equipment_downtime_report(
    IN p_equipment_id VARCHAR(50),
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT
        DATE(dr.start_time) AS downtime_date,
        dr.downtime_type,
        COUNT(*) AS incident_count,
        SUM(dr.duration_minutes) AS total_minutes,
        ROUND(AVG(dr.duration_minutes), 1) AS avg_minutes,
        MIN(dr.duration_minutes) AS min_minutes,
        MAX(dr.duration_minutes) AS max_minutes,
        GROUP_CONCAT(
            CONCAT(dr.report_id, ':', LEFT(dr.description, 30))
            ORDER BY dr.start_time
            SEPARATOR ' | '
        ) AS incident_summary
    FROM downtime_report dr
    WHERE dr.equipment_id = p_equipment_id
        AND DATE(dr.start_time) BETWEEN p_start_date AND p_end_date
        AND dr.duration_minutes IS NOT NULL
    GROUP BY DATE(dr.start_time), dr.downtime_type
    ORDER BY downtime_date DESC, total_minutes DESC;
END$$

DELIMITER ;

-- 调用示例:
-- CALL sp_equipment_downtime_report('EQP-001', '2025-01-01', '2025-01-31');

-- 存储过程2: 计算工单准时完成率
DELIMITER $$

DROP PROCEDURE IF EXISTS sp_on_time_delivery_rate$$
CREATE PROCEDURE sp_on_time_delivery_rate(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT
        DATE(wo.created_at) AS order_date,
        p.product_type,
        COUNT(*) AS total_orders,
        SUM(CASE WHEN wo.status = 'COMPLETED' AND wo.end_time <= DATE_ADD(wo.start_time, INTERVAL p.standard_time MINUTE) THEN 1 ELSE 0 END) AS on_time_orders,
        ROUND(
            SUM(CASE WHEN wo.status = 'COMPLETED' AND wo.end_time <= DATE_ADD(wo.start_time, INTERVAL p.standard_time MINUTE) THEN 1 ELSE 0 END) * 100.0 / COUNT(*),
            1
        ) AS on_time_rate
    FROM work_order wo
    JOIN product p ON wo.product_code = p.product_code
    WHERE DATE(wo.created_at) BETWEEN p_start_date AND p_end_date
        AND wo.start_time IS NOT NULL
    GROUP BY DATE(wo.created_at), p.product_type
    ORDER BY order_date DESC;
END$$

DELIMITER ;

-- 调用示例:
-- CALL sp_on_time_delivery_rate('2025-01-01', '2025-01-31');

-- ============================================
-- 完成
-- ============================================
SELECT '✓ Views and common queries created successfully!' AS STATUS;
