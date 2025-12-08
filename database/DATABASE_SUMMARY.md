# SmartMES Lite Database - Quick Reference

## Quick Start (5 Minutes)

```bash
# 1. Login to MySQL
mysql -u root -p

# 2. Execute all scripts
source /Users/zhanggongqing/project/孵化项目/test_demo/database/schema.sql
source /Users/zhanggongqing/project/孵化项目/test_demo/database/init_data.sql
source /Users/zhanggongqing/project/孵化项目/test_demo/database/views_and_queries.sql

# 3. Verify installation
source /Users/zhanggongqing/project/孵化项目/test_demo/database/verify_database.sql
```

## Database Schema Overview

```
smartmes_lite (Database)
├── product (5 records)           # Semiconductor products
├── equipment (7 records)         # Fabrication equipment
├── user (7 records)              # System users (4 roles)
├── work_order (10 records)       # Production orders
├── downtime_report (8 records)   # Equipment downtime incidents
└── audit_log (6 records)         # System audit trail
```

## Core Tables

| Table | Purpose | Key Fields |
|-------|---------|------------|
| **product** | Product catalog | product_code, product_name, standard_time |
| **equipment** | Equipment master | equipment_id, equipment_name, status, next_maintenance_time |
| **user** | User accounts | user_id, username, role (OPERATOR/SUPERVISOR/ENGINEER/ADMIN) |
| **work_order** | Production orders | order_id, batch_no, plan_qty, actual_qty, status |
| **downtime_report** | Downtime tracking | equipment_id, downtime_type, duration_minutes, status |
| **audit_log** | Audit trail | user_id, operation, module, details |

## Test Data Summary

### Products (5 semiconductor products)
- SM-001: 8-inch Silicon Wafer
- SM-002: 12-inch Silicon Wafer
- SM-003: GaN Epitaxial Wafer
- SM-004: SiC Power Device Wafer
- SM-005: MEMS Sensor Wafer

### Equipment (7 fabrication tools)
- EQP-001: ASML ArF Lithography System (RUNNING)
- EQP-002: LAM Plasma Etcher (RUNNING)
- EQP-003: Applied Materials CVD (IDLE)
- EQP-004: Tokyo Electron Ion Implanter (FAULT) ⚠️
- EQP-005: KLA Tencor Wafer Inspection (RUNNING)
- EQP-006: Disco Wafer Dicing Saw (MAINTENANCE)
- EQP-007: Screen Semi-Auto Printer (IDLE)

### Users (7 users, 4 roles)
- **admin** (ADMIN)
- **supervisor_wang** (SUPERVISOR)
- **engineer_li**, **engineer_zhao** (ENGINEER)
- **operator_chen**, **operator_liu**, **operator_sun** (OPERATOR)

*All passwords: `password123` (BCrypt hashed)*

### Work Orders (10 orders, varied statuses)
- **COMPLETED**: 2 orders (100% done)
- **IN_PROGRESS**: 3 orders (actively running)
- **ABNORMAL**: 1 order (equipment fault)
- **PENDING**: 3 orders (not started)
- **CLOSED**: 1 order (completed & QC passed)

### Downtime Reports (8 incidents)
- Equipment Fault: 4 incidents
- Material Shortage: 2 incidents
- Quality Issue: 1 incident
- Operator Error: 1 incident

## Database Views (Optional)

Created by `views_and_queries.sql`:

| View | Description |
|------|-------------|
| **v_work_order_detail** | Work orders with product, equipment, operator info |
| **v_downtime_detail** | Downtime reports with full context |
| **v_equipment_status** | Equipment status with maintenance alerts |
| **v_daily_production_stats** | Daily production statistics |

## Key Features

### 1. Auto-calculated Fields
- **downtime_report.duration_minutes**: Auto-calculated by trigger when end_time is set

### 2. Indexes for Performance
- work_order: batch_no, status, created_at, product_code, equipment_id
- downtime_report: equipment_id, status, start_time, downtime_type
- user: username, role

### 3. Foreign Key Constraints
- work_order → product (product_code)
- work_order → equipment (equipment_id)
- work_order → user (operator_id)
- downtime_report → work_order (order_id)
- downtime_report → equipment (equipment_id)
- downtime_report → user (reporter_id, responder_id)

### 4. Data Validation
- UTF-8MB4 encoding (full Unicode support)
- NOT NULL constraints on critical fields
- Enum-like VARCHAR fields with specific values
- Timestamp fields with auto-update

## Common Queries

### Active Work Orders
```sql
SELECT * FROM v_work_order_detail
WHERE status = 'IN_PROGRESS'
ORDER BY completion_rate ASC;
```

### Pending Downtime Issues
```sql
SELECT * FROM v_downtime_detail
WHERE status IN ('PENDING', 'PROCESSING')
ORDER BY current_duration_minutes DESC;
```

### Equipment Needing Maintenance
```sql
SELECT * FROM v_equipment_status
WHERE maintenance_alert IN ('OVERDUE', 'URGENT')
ORDER BY days_until_maintenance;
```

### Batch Traceability
```sql
SET @batch_no = 'B20250103';
SELECT * FROM work_order WHERE batch_no = @batch_no;
SELECT * FROM downtime_report dr
JOIN work_order wo ON dr.order_id = wo.order_id
WHERE wo.batch_no = @batch_no;
```

## Connection Strings

### Java (Spring Boot)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartmes_lite?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
```

### Python (SQLAlchemy)
```python
DATABASE_URL = "mysql+pymysql://root:password@localhost:3306/smartmes_lite?charset=utf8mb4"
```

### Node.js (MySQL2)
```javascript
const config = {
  host: 'localhost',
  port: 3306,
  user: 'root',
  password: 'password',
  database: 'smartmes_lite',
  charset: 'utf8mb4'
};
```

## File Sizes

| File | Size | Lines | Description |
|------|------|-------|-------------|
| schema.sql | 9.2KB | 172 | Table definitions |
| init_data.sql | 11KB | 158 | Test data |
| views_and_queries.sql | 14KB | 427 | Views & queries |
| verify_database.sql | 11KB | 388 | Verification script |
| README.md | 9.5KB | 352 | Full documentation |

## Verification

After installation, run:
```bash
mysql -u root -p < verify_database.sql
```

Expected results:
- ✓ 6 tables created
- ✓ 43 test records inserted
- ✓ 0 referential integrity errors
- ✓ 0 data quality issues
- ✓ All character sets: utf8mb4_unicode_ci

## Next Steps

1. **Development**: Configure Spring Boot application.yml with database connection
2. **Testing**: Use test data for unit tests and integration tests
3. **Production**: Clear test data before deployment
4. **Monitoring**: Set up slow query log analysis
5. **Backup**: Configure automated daily backups

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Access denied | `GRANT ALL PRIVILEGES ON smartmes_lite.* TO 'user'@'localhost';` |
| Table exists | `DROP DATABASE smartmes_lite;` then re-run schema.sql |
| Foreign key error | Execute scripts in order: schema → init_data → views |
| Character encoding | Ensure MySQL server default charset is utf8mb4 |

## Support

See full documentation: `README.md`

---

**Database**: smartmes_lite
**Version**: 1.0
**Created**: 2025-12-08
**MySQL**: 8.0+
**Character Set**: UTF-8MB4
**Total Records**: 43 (test data)
