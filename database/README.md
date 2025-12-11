# SmartMES Lite - Database Setup Guide

## Overview

This directory contains MySQL database scripts for the SmartMES Lite (Semiconductor Manufacturing Execution System) application.

## Directory Structure

```
database/
├── README.md               # This file - Setup instructions
├── schema.sql              # Database schema (table definitions)
├── init_data.sql           # Initial test data
├── views_and_queries.sql   # Database views and common queries (optional)
├── init-db.sh              # Docker initialization script
└── my.cnf                  # MySQL configuration file
```

## Prerequisites

- **MySQL Server**: Version 8.0 or higher
- **MySQL Client**: Command-line tool or GUI client (e.g., MySQL Workbench, DBeaver)
- **User Permissions**: CREATE, DROP, INSERT, SELECT privileges

## Database Schema

The database consists of 6 core tables:

| Table Name | Description | Records (Test Data) |
|------------|-------------|---------------------|
| `product` | Product catalog | 5 products |
| `equipment` | Manufacturing equipment | 7 equipment |
| `user` | System users | 7 users |
| `work_order` | Production work orders | 10 orders |
| `downtime_report` | Equipment downtime reports | 8 reports |
| `audit_log` | Audit trail | 6 logs |

### Entity Relationship Diagram

```
product (1) ----< (N) work_order
equipment (1) ----< (N) work_order
equipment (1) ----< (N) downtime_report
user (1) ----< (N) work_order (operator)
user (1) ----< (N) downtime_report (reporter/responder)
work_order (1) ----< (N) downtime_report
```

## Quick Start

### Method 1: Command Line (Recommended)

```bash
# 1. Login to MySQL server
mysql -u root -p

# 2. Execute schema script
mysql> source /Users/zhanggongqing/project/孵化项目/test_demo/database/schema.sql

# 3. Execute initial data script
mysql> source /Users/zhanggongqing/project/孵化项目/test_demo/database/init_data.sql

# 4. (Optional) Execute views and queries script
mysql> source /Users/zhanggongqing/project/孵化项目/test_demo/database/views_and_queries.sql

# 5. Verify installation
mysql> USE smartmes_lite;
mysql> SHOW TABLES;
mysql> SELECT COUNT(*) FROM work_order;
```

### Method 2: Single Command Execution

```bash
# Execute both scripts in one command
mysql -u root -p < /Users/zhanggongqing/project/孵化项目/test_demo/database/schema.sql
mysql -u root -p < /Users/zhanggongqing/project/孵化项目/test_demo/database/init_data.sql
```

### Method 3: Docker (For Development)

```bash
# Start MySQL container with initialization scripts
docker run -d \
  --name smartmes-mysql \
  -e MYSQL_ROOT_PASSWORD=your_password \
  -e MYSQL_DATABASE=smartmes_lite \
  -v $(pwd)/database:/docker-entrypoint-initdb.d \
  -p 3306:3306 \
  mysql:8.0

# Wait 30 seconds for initialization to complete
sleep 30

# Verify
docker exec -it smartmes-mysql mysql -uroot -pyour_password -e "USE smartmes_lite; SHOW TABLES;"
```

## Script Details

### schema.sql

**Features:**
- Creates database `smartmes_lite` with UTF-8MB4 encoding
- Defines 6 tables with proper indexes and foreign keys
- Includes database trigger for automatic downtime duration calculation
- Comprehensive field comments in Chinese

**Key Tables:**
- `work_order`: Stores production orders with status tracking
- `downtime_report`: Records equipment downtime events with auto-calculated duration
- `equipment`: Equipment master data
- `product`: Product catalog
- `user`: User accounts with role-based access control
- `audit_log`: System audit trail

**Indexes:**
- Batch number, status, creation date for work orders
- Equipment ID, status, start time for downtime reports
- Username, role for users

### init_data.sql

**Test Data Overview:**

| Category | Details |
|----------|---------|
| **Products** | 5 semiconductor products (Wafers, Epitaxial, MEMS) |
| **Equipment** | 7 fabrication equipment (Lithography, Etcher, CVD, etc.) |
| **Users** | 7 users with different roles (Admin, Supervisor, Engineer, Operator) |
| **Work Orders** | 10 orders with varied statuses (Pending, In Progress, Completed, Abnormal, Closed) |
| **Downtime Reports** | 8 incidents (Equipment Fault, Material Shortage, Quality Issue, Operator Error) |
| **Audit Logs** | 6 sample audit entries |

**Test User Credentials:**
- All users have password: `password123` (BCrypt hashed)
- Username format: `admin`, `supervisor_wang`, `engineer_li`, `operator_chen`, etc.

**Realistic Scenarios:**
- Work order `WO20250104002` is in ABNORMAL status due to equipment fault
- Equipment `EQP-004` (Ion Implanter) is in FAULT status
- Active downtime report in PROCESSING status
- Equipment `EQP-006` is in scheduled MAINTENANCE

## Database Configuration for Application

### Spring Boot Configuration

Add to `application.yml` or `application.properties`:

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smartmes_lite?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  jpa:
    hibernate:
      ddl-auto: none  # Use manual SQL scripts instead
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
```

Or properties format:

```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartmes_lite?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## Useful Queries

### Check Work Order Status Distribution

```sql
SELECT
    status,
    COUNT(*) AS count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM work_order), 1), '%') AS percentage
FROM work_order
GROUP BY status;
```

### Equipment Downtime Statistics

```sql
SELECT
    e.equipment_name,
    COUNT(dr.report_id) AS downtime_count,
    SUM(dr.duration_minutes) AS total_downtime_minutes,
    ROUND(AVG(dr.duration_minutes), 1) AS avg_downtime_minutes
FROM equipment e
LEFT JOIN downtime_report dr ON e.equipment_id = dr.equipment_id
WHERE dr.duration_minutes IS NOT NULL
GROUP BY e.equipment_id, e.equipment_name
ORDER BY total_downtime_minutes DESC;
```

### Work Order Progress Report

```sql
SELECT
    wo.order_id,
    p.product_name,
    wo.batch_no,
    wo.plan_qty,
    wo.actual_qty,
    CONCAT(ROUND(wo.actual_qty * 100.0 / wo.plan_qty, 1), '%') AS completion_rate,
    wo.status,
    u.real_name AS operator
FROM work_order wo
JOIN product p ON wo.product_code = p.product_code
LEFT JOIN user u ON wo.operator_id = u.user_id
WHERE wo.status IN ('IN_PROGRESS', 'ABNORMAL')
ORDER BY wo.created_at DESC;
```

### Pending Downtime Reports

```sql
SELECT
    dr.report_id,
    wo.order_id,
    e.equipment_name,
    dr.downtime_type,
    dr.description,
    dr.start_time,
    TIMESTAMPDIFF(MINUTE, dr.start_time, NOW()) AS elapsed_minutes,
    u1.real_name AS reporter,
    u2.real_name AS responder
FROM downtime_report dr
JOIN equipment e ON dr.equipment_id = e.equipment_id
LEFT JOIN work_order wo ON dr.order_id = wo.order_id
LEFT JOIN user u1 ON dr.reporter_id = u1.user_id
LEFT JOIN user u2 ON dr.responder_id = u2.user_id
WHERE dr.status != 'RESOLVED'
ORDER BY dr.start_time ASC;
```

## Maintenance

### Backup Database

```bash
# Full backup
mysqldump -u root -p smartmes_lite > smartmes_lite_backup_$(date +%Y%m%d).sql

# Structure only
mysqldump -u root -p --no-data smartmes_lite > smartmes_lite_structure.sql

# Data only
mysqldump -u root -p --no-create-info smartmes_lite > smartmes_lite_data.sql
```

### Reset Database (Caution!)

```bash
# Drop and recreate database
mysql -u root -p -e "DROP DATABASE IF EXISTS smartmes_lite;"
mysql -u root -p < schema.sql
mysql -u root -p < init_data.sql
```

### Clear Test Data (Keep Schema)

```sql
USE smartmes_lite;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE audit_log;
TRUNCATE TABLE downtime_report;
TRUNCATE TABLE work_order;
TRUNCATE TABLE user;
TRUNCATE TABLE equipment;
TRUNCATE TABLE product;
SET FOREIGN_KEY_CHECKS = 1;
```

## Troubleshooting

### Issue: "Access denied for user"

```bash
# Grant privileges
mysql -u root -p
mysql> CREATE USER 'smartmes'@'localhost' IDENTIFIED BY 'password';
mysql> GRANT ALL PRIVILEGES ON smartmes_lite.* TO 'smartmes'@'localhost';
mysql> FLUSH PRIVILEGES;
```

### Issue: "Table already exists"

```bash
# Drop existing database first
mysql -u root -p -e "DROP DATABASE IF EXISTS smartmes_lite;"
# Then re-run schema.sql
```

### Issue: Foreign key constraint fails

Make sure to execute scripts in order:
1. `schema.sql` first (creates tables with foreign key constraints)
2. `init_data.sql` second (inserts data respecting relationships)

### Issue: Character encoding problems

Ensure MySQL server is configured for UTF-8MB4:

```sql
-- Check current settings
SHOW VARIABLES LIKE 'character_set%';
SHOW VARIABLES LIKE 'collation%';

-- Should show utf8mb4 for:
-- character_set_database
-- character_set_server
-- collation_database
-- collation_server
```

## Support

For issues or questions:
1. Check MySQL error logs: `/var/log/mysql/error.log`
2. Verify MySQL version: `mysql --version` (should be 8.0+)
3. Check table structure: `DESCRIBE table_name;`
4. Review foreign key constraints: `SHOW CREATE TABLE table_name;`

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2025-12-08 | Initial release with 6 core tables |

---

**Database Name**: `smartmes_lite`
**Default Port**: 3306
**Character Set**: UTF-8MB4
**Collation**: utf8mb4_unicode_ci
**Engine**: InnoDB
