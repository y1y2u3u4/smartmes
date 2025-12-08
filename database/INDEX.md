# SmartMES Lite Database - File Index

## Quick Navigation

| File | Purpose | When to Use |
|------|---------|-------------|
| **[README.md](README.md)** | Complete setup guide | First-time installation |
| **[DATABASE_SUMMARY.md](DATABASE_SUMMARY.md)** | Quick reference | Daily reference |
| **[schema.sql](schema.sql)** | Create tables | Initial setup |
| **[init_data.sql](init_data.sql)** | Insert test data | Development/Testing |
| **[views_and_queries.sql](views_and_queries.sql)** | Create views/queries | Optional enhancement |
| **[verify_database.sql](verify_database.sql)** | Verify installation | After setup |
| **[init-db.sh](init-db.sh)** | Docker initialization | Docker deployment |
| **[my.cnf](my.cnf)** | MySQL configuration | Production tuning |

## Execution Order

### Standard Installation
```bash
# Step 1: Create tables and triggers
mysql -u root -p < schema.sql

# Step 2: Insert test data
mysql -u root -p < init_data.sql

# Step 3: Create views and procedures (optional)
mysql -u root -p < views_and_queries.sql

# Step 4: Verify installation
mysql -u root -p < verify_database.sql
```

### Docker Installation
```bash
# Use the automated script
./init-db.sh
```

## File Details

### schema.sql (172 lines)
**Purpose**: Database schema definition

**Contains**:
- Database creation (smartmes_lite)
- 6 table definitions:
  - product (产品表)
  - equipment (设备表)
  - user (用户表)
  - work_order (工单表)
  - downtime_report (停机上报表)
  - audit_log (审计日志表)
- Indexes for performance optimization
- Foreign key constraints
- Trigger: Auto-calculate downtime duration

**Key Features**:
- UTF-8MB4 character set
- InnoDB storage engine
- Comprehensive field comments in Chinese
- Referential integrity enforcement

---

### init_data.sql (158 lines)
**Purpose**: Initial test data insertion

**Contains**:
- 5 semiconductor products (SM-001 to SM-005)
- 7 fabrication equipment (EQP-001 to EQP-007)
- 7 users with different roles
- 10 work orders with varied statuses
- 8 downtime reports
- 6 audit log entries

**Test Scenarios**:
- Completed production runs
- Active work orders in progress
- Equipment fault situation (EQP-004)
- Pending downtime issues
- Resolved incidents

**Data Validation Queries**: Included at the end

---

### views_and_queries.sql (427 lines)
**Purpose**: Database views and common queries

**Contains**:
- 4 database views:
  - `v_work_order_detail`: Complete work order information
  - `v_downtime_detail`: Downtime reports with context
  - `v_equipment_status`: Equipment status with alerts
  - `v_daily_production_stats`: Daily statistics
- 8 common query examples:
  - Active work orders
  - Pending downtime issues
  - Equipment OEE calculation
  - Downtime Pareto analysis
  - Batch traceability
  - Operator performance
  - Production line analysis
  - Maintenance scheduling
- 2 stored procedures:
  - `sp_equipment_downtime_report`: Generate downtime reports
  - `sp_on_time_delivery_rate`: Calculate on-time delivery

**Use Cases**:
- Dashboard data queries
- Management reports
- Data analysis
- Performance monitoring

---

### verify_database.sql (388 lines)
**Purpose**: Comprehensive database verification

**Verification Checks**:
1. Table structure verification
2. Index verification
3. Foreign key constraints
4. Data integrity check
5. Referential integrity check
6. Data quality check
7. Trigger verification
8. View verification
9. Stored procedure verification
10. Character set verification

**Output**: Detailed verification report with recommendations

---

### init-db.sh (204 lines)
**Purpose**: Automated Docker database initialization

**Features**:
- Wait for MySQL to be ready
- Check if database already initialized
- Execute schema and data scripts
- Verify initialization results
- Color-coded output
- Error handling

**Environment Variables**:
- DB_HOST, DB_PORT, DB_NAME
- DB_USER, DB_PASSWORD
- DB_ROOT_PASSWORD

---

### my.cnf (40 lines)
**Purpose**: MySQL server configuration

**Settings**:
- Character set: UTF-8MB4
- Time zone: Asia/Shanghai (+08:00)
- Connection limits: 200 max connections
- Performance tuning:
  - InnoDB buffer pool: 256MB
  - Log file size: 64MB
- Slow query logging enabled (>2 seconds)
- Binary logging for backup/recovery

---

## Database Statistics

| Metric | Value |
|--------|-------|
| Database Name | smartmes_lite |
| Total Tables | 6 |
| Total Indexes | ~15 |
| Total Foreign Keys | 7 |
| Total Triggers | 1 |
| Total Views | 4 (optional) |
| Total Stored Procedures | 2 (optional) |
| Test Data Records | 43 |
| Character Set | UTF-8MB4 |
| Collation | utf8mb4_unicode_ci |
| Storage Engine | InnoDB |

## Tables Details

| Table | Columns | Indexes | Foreign Keys | Test Records |
|-------|---------|---------|--------------|--------------|
| product | 6 | 0 | 0 | 5 |
| equipment | 9 | 2 | 0 | 7 |
| user | 9 | 2 | 0 | 7 |
| work_order | 14 | 5 | 3 | 10 |
| downtime_report | 14 | 4 | 4 | 8 |
| audit_log | 9 | 4 | 0 | 6 |

## Database Relationships

```
product (1) ----< (N) work_order
equipment (1) ----< (N) work_order
equipment (1) ----< (N) downtime_report
user (1) ----< (N) work_order (operator)
user (1) ----< (N) downtime_report (reporter)
user (1) ----< (N) downtime_report (responder)
work_order (1) ----< (N) downtime_report
```

## Common Workflows

### Development Setup
1. Read: README.md
2. Execute: schema.sql
3. Execute: init_data.sql
4. Execute: views_and_queries.sql (optional)
5. Verify: verify_database.sql

### Production Deployment
1. Review: my.cnf (tune for production)
2. Execute: schema.sql
3. Skip: init_data.sql (don't use test data)
4. Execute: views_and_queries.sql (recommended)
5. Configure: Automated backups
6. Monitor: Slow query log

### Testing Environment
1. Use: Docker + init-db.sh
2. Full test data available
3. Views and procedures included
4. Easy reset: Drop and recreate

### Database Maintenance
1. Regular: Run verify_database.sql
2. Monthly: Review slow query log
3. Quarterly: Optimize tables
4. Annually: Review index usage

## Integration Examples

### Spring Boot Configuration
See: README.md § "Database Configuration for Application"

### Docker Compose
```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: smartmes_lite
      MYSQL_ROOT_PASSWORD: your_password
    volumes:
      - ./database/schema.sql:/docker-entrypoint-initdb.d/1-schema.sql
      - ./database/init_data.sql:/docker-entrypoint-initdb.d/2-init_data.sql
      - ./database/my.cnf:/etc/mysql/conf.d/custom.cnf
```

## Troubleshooting

See detailed troubleshooting guide in README.md

Quick fixes:
- **Access denied**: Check user privileges
- **Table exists**: Drop database and recreate
- **Foreign key error**: Execute scripts in correct order
- **Character encoding**: Verify MySQL server charset

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2025-12-08 | Initial release with 6 core tables |

## Contact & Support

For issues or questions:
- Review: README.md for detailed documentation
- Check: DATABASE_SUMMARY.md for quick reference
- Run: verify_database.sql to diagnose issues

---

**Last Updated**: 2025-12-08
**MySQL Version**: 8.0+
**Total Scripts**: 8 files
**Total Size**: ~84KB
