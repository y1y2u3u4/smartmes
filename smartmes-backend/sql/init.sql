-- SmartMES Lite 数据库初始化脚本
-- 版本: 1.0.0
-- 作者: SmartMES Team
-- 日期: 2023-12-08

-- 创建数据库
CREATE DATABASE IF NOT EXISTS smartmes_lite
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE smartmes_lite;

-- 创建工单表
CREATE TABLE IF NOT EXISTS work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    work_order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工单号（唯一）',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_code VARCHAR(50) COMMENT '产品编号',
    product_name VARCHAR(100) COMMENT '产品名称',
    line_id VARCHAR(50) COMMENT '生产产线ID',
    plan_qty INT NOT NULL COMMENT '计划产量',
    actual_qty INT NOT NULL DEFAULT 0 COMMENT '实际产量',
    qualified_qty INT NOT NULL DEFAULT 0 COMMENT '合格数量',
    defect_qty INT NOT NULL DEFAULT 0 COMMENT '不合格数量',
    status VARCHAR(20) NOT NULL COMMENT '工单状态：PENDING-待开始, IN_PROGRESS-进行中, COMPLETED-已完成, ABNORMAL-异常, CANCELLED-已取消',
    plan_start_time DATETIME COMMENT '计划开始时间',
    plan_end_time DATETIME COMMENT '计划完成时间',
    actual_start_time DATETIME COMMENT '实际开始时间',
    actual_end_time DATETIME COMMENT '实际完成时间',
    priority INT DEFAULT 3 COMMENT '优先级（1-5，数字越大优先级越高）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remarks VARCHAR(500) COMMENT '备注信息',
    INDEX idx_work_order_no (work_order_no),
    INDEX idx_product_code (product_code),
    INDEX idx_line_id (line_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_plan_start_time (plan_start_time),
    INDEX idx_plan_end_time (plan_end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- 插入测试数据
INSERT INTO work_order (
    work_order_no, product_id, product_code, product_name, line_id,
    plan_qty, actual_qty, qualified_qty, defect_qty, status,
    plan_start_time, plan_end_time, priority, remarks
) VALUES
('WO20231208001', 1, 'P001', 'Product A', 'LINE001', 1000, 0, 0, 0, 'PENDING',
 '2023-12-08 08:00:00', '2023-12-08 18:00:00', 5, 'Test work order 1'),

('WO20231208002', 2, 'P002', 'Product B', 'LINE001', 2000, 500, 490, 10, 'IN_PROGRESS',
 '2023-12-08 09:00:00', '2023-12-08 19:00:00', 4, 'Test work order 2'),

('WO20231208003', 3, 'P003', 'Product C', 'LINE002', 1500, 1500, 1480, 20, 'COMPLETED',
 '2023-12-07 08:00:00', '2023-12-07 18:00:00', 3, 'Test work order 3'),

('WO20231208004', 1, 'P001', 'Product A', 'LINE002', 800, 300, 295, 5, 'IN_PROGRESS',
 '2023-12-08 10:00:00', '2023-12-08 20:00:00', 3, 'Test work order 4'),

('WO20231208005', 4, 'P004', 'Product D', 'LINE003', 3000, 0, 0, 0, 'PENDING',
 '2023-12-09 08:00:00', '2023-12-09 18:00:00', 2, 'Test work order 5');

-- 查询验证
SELECT * FROM work_order ORDER BY create_time DESC;

-- 统计各状态工单数量
SELECT
    status AS '工单状态',
    COUNT(*) AS '数量'
FROM work_order
GROUP BY status;

-- 统计各产线工单情况
SELECT
    line_id AS '产线',
    COUNT(*) AS '工单总数',
    SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS '待开始',
    SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS '进行中',
    SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS '已完成'
FROM work_order
GROUP BY line_id;
