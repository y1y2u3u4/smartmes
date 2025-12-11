-- ============================================
-- SmartMES Lite - Database Schema
-- Description: 半导体生产工单跟踪系统数据库表结构
-- Version: 1.0
-- Created: 2025-12-08
-- Database: MySQL 8.0+
-- ============================================

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS smartmes_lite DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smartmes_lite;

-- ============================================
-- 1. 产品表 (product)
-- Description: 存储产品基础信息
-- ============================================
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    product_code VARCHAR(50) PRIMARY KEY COMMENT '产品编码',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    product_type VARCHAR(50) COMMENT '产品类型',
    standard_time INT COMMENT '标准工时（分钟）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';

-- ============================================
-- 2. 设备表 (equipment)
-- Description: 存储设备档案信息
-- ============================================
DROP TABLE IF EXISTS equipment;
CREATE TABLE equipment (
    equipment_id VARCHAR(50) PRIMARY KEY COMMENT '设备ID',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    equipment_type VARCHAR(50) COMMENT '设备类型',
    line_id VARCHAR(50) COMMENT '产线ID',
    status VARCHAR(20) NOT NULL DEFAULT 'IDLE' COMMENT '设备状态: RUNNING-运行中, IDLE-空闲, FAULT-故障, MAINTENANCE-维护中',
    last_maintenance_time DATETIME COMMENT '最后维护时间',
    next_maintenance_time DATETIME COMMENT '下次计划维护时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_line_id (line_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- ============================================
-- 3. 用户表 (user)
-- Description: 存储用户信息和权限
-- ============================================
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    user_id VARCHAR(50) PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名（登录账号）',
    password VARCHAR(200) NOT NULL COMMENT '密码（加密存储）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL COMMENT '角色: OPERATOR-操作员, SUPERVISOR-主管, ENGINEER-工程师, ADMIN-管理员',
    team VARCHAR(50) COMMENT '班组/团队',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '用户状态: ACTIVE-启用, INACTIVE-停用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 4. 工单表 (work_order)
-- Description: 存储生产工单信息
-- ============================================
DROP TABLE IF EXISTS work_order;
CREATE TABLE work_order (
    order_id VARCHAR(50) PRIMARY KEY COMMENT '工单号',
    product_code VARCHAR(50) NOT NULL COMMENT '产品编码',
    batch_no VARCHAR(50) COMMENT '批次号',
    plan_qty INT NOT NULL COMMENT '计划数量',
    actual_qty INT DEFAULT 0 COMMENT '实际完成数量',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '工单状态: PENDING-待开始, IN_PROGRESS-进行中, COMPLETED-已完成, CLOSED-已关闭, ABNORMAL-异常',
    line_id VARCHAR(50) COMMENT '产线ID',
    equipment_id VARCHAR(50) COMMENT '设备ID',
    operator_id VARCHAR(50) COMMENT '操作员ID',
    created_by VARCHAR(50) NOT NULL COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_batch_no (batch_no) COMMENT '批次号索引，用于批次追溯查询',
    INDEX idx_status (status) COMMENT '状态索引，用于工单状态筛选',
    INDEX idx_created_at (created_at) COMMENT '创建时间索引，用于时间范围查询',
    INDEX idx_product_code (product_code) COMMENT '产品编码索引',
    INDEX idx_equipment_id (equipment_id) COMMENT '设备ID索引',
    FOREIGN KEY (product_code) REFERENCES product(product_code) ON DELETE RESTRICT,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id) ON DELETE SET NULL,
    FOREIGN KEY (operator_id) REFERENCES user(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生产工单表';

-- ============================================
-- 5. 停机上报表 (downtime_report)
-- Description: 存储异常停机记录
-- ============================================
DROP TABLE IF EXISTS downtime_report;
CREATE TABLE downtime_report (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '上报记录ID',
    order_id VARCHAR(50) COMMENT '关联工单号（可为空，设备异常可能不关联具体工单）',
    equipment_id VARCHAR(50) NOT NULL COMMENT '设备ID',
    downtime_type VARCHAR(50) NOT NULL COMMENT '停机类型: EQUIPMENT_FAULT-设备故障, MATERIAL_SHORTAGE-物料缺料, QUALITY_ISSUE-质量异常, OPERATOR_ERROR-人员操作失误, OTHER-其他',
    description TEXT COMMENT '异常描述',
    start_time DATETIME NOT NULL COMMENT '停机开始时间',
    end_time DATETIME COMMENT '停机结束时间',
    duration_minutes INT COMMENT '停机时长（分钟，自动计算）',
    reporter_id VARCHAR(50) NOT NULL COMMENT '上报人ID',
    responder_id VARCHAR(50) COMMENT '响应人ID',
    solution TEXT COMMENT '处理措施',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态: PENDING-待处理, PROCESSING-处理中, RESOLVED-已解决',
    attachments VARCHAR(500) COMMENT '附件路径（JSON格式存储多个文件路径）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_equipment_id (equipment_id) COMMENT '设备ID索引，用于设备异常统计',
    INDEX idx_status (status) COMMENT '状态索引，用于待处理异常查询',
    INDEX idx_start_time (start_time) COMMENT '停机时间索引，用于时间范围统计',
    INDEX idx_downtime_type (downtime_type) COMMENT '停机类型索引，用于异常分类统计',
    FOREIGN KEY (order_id) REFERENCES work_order(order_id) ON DELETE SET NULL,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id) ON DELETE RESTRICT,
    FOREIGN KEY (reporter_id) REFERENCES user(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (responder_id) REFERENCES user(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='停机异常上报表';

-- ============================================
-- 6. 审计日志表 (audit_log)
-- Description: 记录关键操作的审计日志
-- ============================================
DROP TABLE IF EXISTS audit_log;
CREATE TABLE audit_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id VARCHAR(50) COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '操作用户名',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型: CREATE-创建, UPDATE-修改, DELETE-删除, LOGIN-登录, LOGOUT-登出',
    module VARCHAR(50) NOT NULL COMMENT '操作模块: WORK_ORDER-工单, DOWNTIME-异常, EQUIPMENT-设备, USER-用户',
    entity_id VARCHAR(100) COMMENT '操作实体ID',
    details TEXT COMMENT '操作详情（JSON格式）',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引',
    INDEX idx_operation (operation) COMMENT '操作类型索引',
    INDEX idx_module (module) COMMENT '模块索引',
    INDEX idx_created_at (created_at) COMMENT '时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- ============================================
-- 触发器：自动计算停机时长
-- ============================================
DELIMITER $$

DROP TRIGGER IF EXISTS trg_calc_downtime_duration$$
CREATE TRIGGER trg_calc_downtime_duration
BEFORE UPDATE ON downtime_report
FOR EACH ROW
BEGIN
    IF NEW.end_time IS NOT NULL AND NEW.start_time IS NOT NULL THEN
        SET NEW.duration_minutes = TIMESTAMPDIFF(MINUTE, NEW.start_time, NEW.end_time);
    END IF;
END$$

DELIMITER ;

-- ============================================
-- 初始化完成
-- ============================================
SELECT 'Database schema created successfully!' AS STATUS;
