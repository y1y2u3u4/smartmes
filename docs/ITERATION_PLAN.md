# SmartMES Lite Iteration Plan

> Version: 1.0 | Date: 2025-01-09 | Status: Draft

---

## Table of Contents

1. [System Status Overview](#1-system-status-overview)
2. [Role-Based Task Allocation](#2-role-based-task-allocation)
3. [Sprint Planning](#3-sprint-planning)
4. [Quick Wins](#4-quick-wins)
5. [Technical Debt](#5-technical-debt)
6. [Test Data Strategy](#6-test-data-strategy)
7. [Next Steps](#7-next-steps)

---

## 1. System Status Overview

### 1.1 Module Completion Matrix

| Module | Frontend | Backend | Database | API Integration | Overall |
|--------|----------|---------|----------|-----------------|---------|
| Work Order Management | ✅ | ✅ | ✅ | ⚠️ Partial | 75% |
| Base Data (Product/Equipment) | ✅ | ✅ | ✅ | ✅ | 100% |
| Production Dashboard | ✅ | ✅ | ✅ | ⚠️ Partial | 70% |
| Downtime Management | ✅ | ✅ | ✅ | ❌ | 50% |
| User Authentication | ❌ | ⚠️ Basic | ✅ | ❌ | 20% |

### 1.2 Core Issues Identified

| Issue | Impact | Priority |
|-------|--------|----------|
| Field naming inconsistency (snake_case vs camelCase) | Frontend-backend integration failures | 🔴 High |
| Edit mode doesn't load existing data | Users cannot edit work orders | 🔴 High |
| Delete confirmation shows "undefined" | Poor UX, confusing for users | 🔴 High |
| Search functionality not implemented | Users cannot filter work orders | 🟡 Medium |
| Downtime API not integrated | Module unusable | 🟡 Medium |
| No user authentication | Security vulnerability | 🟢 Low (for MVP) |

### 1.3 Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        SmartMES Lite                             │
├─────────────────────────────────────────────────────────────────┤
│  Frontend (Vue 3 + Vite + Element Plus)                         │
│  ├── views/WorkOrder/         # Work order pages                │
│  ├── views/Dashboard/         # Production dashboard            │
│  ├── views/Downtime/          # Downtime management             │
│  ├── components/              # Reusable components             │
│  └── api/                     # API layer (needs downtime.js)   │
├─────────────────────────────────────────────────────────────────┤
│  Backend (Java 17 + Spring Boot 3.2)                            │
│  ├── controller/              # REST controllers                │
│  ├── service/                 # Business logic                  │
│  ├── repository/              # Data access                     │
│  └── entity/                  # JPA entities                    │
├─────────────────────────────────────────────────────────────────┤
│  Database (MySQL 8.0)                                           │
│  ├── work_order               # 10 test records                 │
│  ├── equipment                # 7 records                       │
│  ├── product                  # 5 records                       │
│  ├── downtime_report          # 8 records                       │
│  └── user                     # 7 records                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Role-Based Task Allocation

### 2.1 Frontend Developer Tasks

#### Sprint 1: Data Layer Fix (Priority: 🔴 High)

| Task ID | File | Description | Effort |
|---------|------|-------------|--------|
| F1.1 | `src/utils/dataAdapter.js` | **NEW** Create data adapter for field mapping | 2h |
| F1.2 | `src/api/downtime.js` | **NEW** Downtime API module | 1h |
| F1.3 | `src/api/workorder.js` | Integrate data adapter in API calls | 1h |

**Data Adapter Implementation:**

```javascript
// src/utils/dataAdapter.js
export const workOrderAdapter = {
  // Convert frontend format to backend format
  toBackend: (data) => ({
    id: data.work_order_number,
    productCode: data.product_code,
    batchNo: data.batch_number,
    planQty: data.planned_quantity,
    actualQty: data.actual_quantity,
    lineId: data.production_line,
    equipmentId: data.equipment,
    operatorId: data.operator,
    status: data.status,
    startTime: data.start_time,
    endTime: data.end_time
  }),

  // Convert backend format to frontend format
  toFrontend: (data) => ({
    work_order_number: data.id || data.workOrderNo,
    product_code: data.productCode,
    batch_number: data.batchNo,
    planned_quantity: data.planQty,
    actual_quantity: data.actualQty,
    production_line: data.lineId,
    equipment: data.equipmentId,
    operator: data.operatorId,
    status: data.status,
    start_time: data.startTime,
    end_time: data.endTime,
    created_at: data.createdAt,
    completion_rate: data.completionRate
  }),

  // Convert array of records
  toFrontendList: (records) => records.map(r => workOrderAdapter.toFrontend(r))
}
```

#### Sprint 2: Feature Enhancement (Priority: 🟡 Medium)

| Task ID | File | Description | Effort |
|---------|------|-------------|--------|
| F2.1 | `WorkOrderList.vue` | Add status action buttons (Start/Complete/Cancel) | 3h |
| F2.2 | `CreateWorkOrder.vue` | Fix edit mode data loading | 2h |
| F2.3 | `WorkOrderList.vue` | Implement search functionality | 2h |
| F2.4 | `DowntimeList.vue` | Integrate downtime API | 2h |
| F2.5 | `DowntimeReport.vue` | Implement report submission | 2h |

#### Sprint 3: User Experience (Priority: 🟢 Low)

| Task ID | File | Description | Effort |
|---------|------|-------------|--------|
| F3.1 | `views/Login.vue` | **NEW** Login page | 3h |
| F3.2 | `src/store/user.js` | **NEW** User state management | 2h |
| F3.3 | `src/router/guards.js` | **NEW** Route guards for auth | 1h |
| F3.4 | `components/` | Add loading states and error handling | 2h |

---

### 2.2 Backend Developer Tasks

#### Sprint 1: API Standardization (Priority: 🔴 High)

| Task ID | File | Description | Effort |
|---------|------|-------------|--------|
| B1.1 | `WorkOrder.java` | Add `@JsonProperty` annotations for field aliases | 1h |
| B1.2 | `DowntimeController.java` | Complete REST API endpoints | 2h |
| B1.3 | `ApiResponse.java` | Standardize response format | 1h |

**Entity Field Mapping Example:**

```java
// WorkOrder.java
@Entity
@Table(name = "work_order")
public class WorkOrder {

    @Id
    @Column(name = "order_id")
    @JsonProperty("work_order_number")  // Frontend field name
    @JsonAlias({"id", "orderId"})       // Accept multiple input names
    private String id;

    @Column(name = "plan_qty")
    @JsonProperty("planned_quantity")
    private Integer planQty;

    @Column(name = "actual_qty")
    @JsonProperty("actual_quantity")
    private Integer actualQty;

    @Column(name = "batch_no")
    @JsonProperty("batch_number")
    private String batchNo;

    // ... other fields
}
```

#### Sprint 2: Business Enhancement (Priority: 🟡 Medium)

| Task ID | File | Description | Effort |
|---------|------|-------------|--------|
| B2.1 | `UserController.java` | **NEW** User management API | 3h |
| B2.2 | `AuthController.java` | **NEW** Authentication API (login/logout) | 3h |
| B2.3 | `WorkOrderServiceImpl.java` | Add state machine validation | 2h |
| B2.4 | `DowntimeServiceImpl.java` | Complete business logic | 2h |

#### Sprint 3: Security & Optimization (Priority: 🟢 Low)

| Task ID | File | Description | Effort |
|---------|------|-------------|--------|
| B3.1 | `SecurityConfig.java` | **NEW** Spring Security configuration | 3h |
| B3.2 | `JwtTokenProvider.java` | **NEW** JWT utility class | 2h |
| B3.3 | `GlobalExceptionHandler.java` | Enhance exception handling | 1h |
| B3.4 | `CacheConfig.java` | **NEW** Add caching layer | 2h |

---

### 2.3 Database Administrator Tasks

#### Sprint 1: Data Integrity (Priority: 🔴 High)

| Task ID | Description | Effort |
|---------|-------------|--------|
| D1.1 | Verify foreign key constraints | 1h |
| D1.2 | Add missing indexes for query optimization | 1h |
| D1.3 | Create database backup script | 1h |

**Index Optimization Script:**

```sql
-- Optimize work_order queries
CREATE INDEX IF NOT EXISTS idx_work_order_status_created
ON work_order(status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_work_order_product_code
ON work_order(product_code);

CREATE INDEX IF NOT EXISTS idx_work_order_line_id
ON work_order(line_id);

-- Optimize downtime queries
CREATE INDEX IF NOT EXISTS idx_downtime_equipment_status
ON downtime_report(equipment_id, status);

CREATE INDEX IF NOT EXISTS idx_downtime_start_time
ON downtime_report(start_time DESC);
```

#### Sprint 2: Test Data Generation (Priority: 🟡 Medium)

| Task ID | Description | Effort |
|---------|-------------|--------|
| D2.1 | Generate 100+ work order test records | 1h |
| D2.2 | Create boundary test data (nulls, extremes) | 1h |
| D2.3 | Prepare performance test dataset | 2h |

**Batch Test Data Generation Script:**

```sql
-- Generate 100 test work orders with various statuses
DELIMITER //
CREATE PROCEDURE GenerateTestWorkOrders(IN num_records INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE statuses VARCHAR(100) DEFAULT 'PENDING,IN_PROGRESS,COMPLETED,ABNORMAL,CANCELLED';
    DECLARE products VARCHAR(100) DEFAULT 'SM-001,SM-002,SM-003,SM-004,SM-005';
    DECLARE lines VARCHAR(50) DEFAULT 'LINE-01,LINE-02,LINE-03';

    WHILE i <= num_records DO
        INSERT INTO work_order (
            order_id,
            product_code,
            batch_no,
            plan_qty,
            actual_qty,
            status,
            line_id,
            equipment_id,
            operator_id,
            created_at
        ) VALUES (
            CONCAT('WO-TEST-', LPAD(i, 4, '0')),
            ELT(FLOOR(1 + RAND() * 5), 'SM-001', 'SM-002', 'SM-003', 'SM-004', 'SM-005'),
            CONCAT('BATCH-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(i, 3, '0')),
            FLOOR(100 + RAND() * 900),
            FLOOR(RAND() * 500),
            ELT(FLOOR(1 + RAND() * 5), 'PENDING', 'IN_PROGRESS', 'COMPLETED', 'ABNORMAL', 'CANCELLED'),
            CONCAT('LINE-0', FLOOR(1 + RAND() * 3)),
            CONCAT('EQP-00', FLOOR(1 + RAND() * 7)),
            CONCAT('U00', FLOOR(1 + RAND() * 7)),
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)
        );
        SET i = i + 1;
    END WHILE;
END //
DELIMITER ;

-- Execute: CALL GenerateTestWorkOrders(100);
```

---

### 2.4 QA Engineer Tasks

#### Sprint 1: Functional Testing (Priority: 🔴 High)

| Task ID | Test Scope | Test Cases |
|---------|-----------|------------|
| Q1.1 | Work Order CRUD | 15+ |
| Q1.2 | Work Order Status Flow | 10+ |
| Q1.3 | Data Boundary Validation | 20+ |

**Test Case Examples:**

```markdown
## Work Order Test Cases

### TC-WO-001: Create Work Order - Happy Path
**Precondition:** User is on Work Order List page
**Steps:**
1. Click "Create Work Order" button
2. Fill in all required fields:
   - Work Order No: Auto-generated
   - Product Code: Select "SM-001"
   - Batch Number: "BATCH-TEST-001"
   - Planned Quantity: 100
   - Production Line: Select "Production Line 1"
   - Equipment: Select any equipment
   - Operator: Select any operator
3. Click "Create" button
**Expected Result:**
- Success message displayed
- Redirect to Work Order List
- New work order appears in the list

### TC-WO-002: Create Work Order - Missing Required Field
**Steps:**
1. Click "Create Work Order" button
2. Leave "Product Code" empty
3. Click "Create" button
**Expected Result:**
- Validation error displayed: "Product Code is required"
- Form not submitted

### TC-WO-003: Edit Work Order
**Precondition:** At least one work order exists
**Steps:**
1. Click "Edit" on any work order row
2. Verify all fields are pre-populated
3. Change Planned Quantity to 200
4. Click "Update" button
**Expected Result:**
- Success message displayed
- Work order updated with new quantity

### TC-WO-004: Delete Work Order - Confirm
**Steps:**
1. Click "Delete" on any work order row
2. Verify confirmation dialog shows correct work order number
3. Click "Delete" to confirm
**Expected Result:**
- Work order removed from list
- Success message displayed

### TC-WO-005: Delete Work Order - Cancel
**Steps:**
1. Click "Delete" on any work order row
2. Click "Cancel" in confirmation dialog
**Expected Result:**
- Dialog closes
- Work order still exists in list
```

#### Sprint 2: Integration Testing (Priority: 🟡 Medium)

| Task ID | Test Scope | Description |
|---------|-----------|-------------|
| Q2.1 | Frontend-Backend API | Verify all API endpoints |
| Q2.2 | Database Transactions | Test data consistency |
| Q2.3 | Concurrent Operations | Multiple users scenario |

#### Sprint 3: Automation Testing (Priority: 🟢 Low)

| Task ID | Tool | Description |
|---------|------|-------------|
| Q3.1 | Playwright | E2E test scripts |
| Q3.2 | Jest | Frontend unit tests |
| Q3.3 | JUnit | Backend unit tests |

**Playwright E2E Test Example:**

```javascript
// tests/e2e/workorder.spec.js
import { test, expect } from '@playwright/test';

test.describe('Work Order Management', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5175/workorders');
  });

  test('should display work order list', async ({ page }) => {
    await expect(page.locator('h2')).toHaveText('Work Order Management');
    await expect(page.locator('table')).toBeVisible();
  });

  test('should create new work order', async ({ page }) => {
    await page.click('button:has-text("Create Work Order")');
    await expect(page).toHaveURL(/\/workorders\/create/);

    // Fill form
    await page.fill('[placeholder="Enter batch number"]', 'TEST-BATCH-001');
    await page.fill('input[role="spinbutton"]', '100');

    // Select product
    await page.click('.el-select:has-text("Select product")');
    await page.click('.el-select-dropdown__item:first-child');

    // Select production line
    await page.locator('.el-form-item:has-text("Production Line") .el-select').click();
    await page.click('.el-select-dropdown__item:first-child');

    // Select equipment
    await page.locator('.el-form-item:has-text("Equipment") .el-select').click();
    await page.click('.el-select-dropdown__item:first-child');

    // Select operator
    await page.locator('.el-form-item:has-text("Operator") .el-select').click();
    await page.click('.el-select-dropdown__item:first-child');

    // Submit
    await page.click('button:has-text("Create")');

    // Verify success
    await expect(page.locator('.el-message--success')).toBeVisible();
    await expect(page).toHaveURL('/workorders');
  });

  test('should delete work order with confirmation', async ({ page }) => {
    const deleteButton = page.locator('button:has-text("Delete")').first();
    await deleteButton.click();

    // Verify confirmation dialog
    await expect(page.locator('.el-message-box')).toBeVisible();
    await expect(page.locator('.el-message-box__message')).toContainText('Are you sure');

    // Cancel deletion
    await page.click('button:has-text("Cancel")');
    await expect(page.locator('.el-message-box')).not.toBeVisible();
  });
});
```

---

## 3. Sprint Planning

### Sprint Timeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                    SmartMES Iteration Timeline                       │
├─────────────────────────────────────────────────────────────────────┤
│ Sprint 1: Data Layer Fix & Core Functions                            │
│ Duration: 1 week                                                     │
│ ├── Frontend: F1.1-F1.3 (Data adapter, Downtime API)                │
│ ├── Backend:  B1.1-B1.3 (API standardization)                       │
│ ├── DBA:      D1.1-D1.3 (Data integrity)                            │
│ └── QA:       Q1.1-Q1.3 (Functional testing)                        │
├─────────────────────────────────────────────────────────────────────┤
│ Sprint 2: Feature Enhancement & Integration                          │
│ Duration: 1-2 weeks                                                  │
│ ├── Frontend: F2.1-F2.5 (Status actions, Edit fix, Search)          │
│ ├── Backend:  B2.1-B2.4 (User API, State machine)                   │
│ ├── DBA:      D2.1-D2.3 (Test data generation)                      │
│ └── QA:       Q2.1-Q2.3 (Integration testing)                       │
├─────────────────────────────────────────────────────────────────────┤
│ Sprint 3: Security & Optimization                                    │
│ Duration: 1-2 weeks                                                  │
│ ├── Frontend: F3.1-F3.4 (Login, Auth guards)                        │
│ ├── Backend:  B3.1-B3.4 (Security config, JWT)                      │
│ └── QA:       Q3.1-Q3.3 (Automation tests)                          │
└─────────────────────────────────────────────────────────────────────┘
```

### Sprint 1 Deliverables

| Deliverable | Owner | Acceptance Criteria |
|-------------|-------|---------------------|
| Data Adapter | Frontend | All API calls use adapter, no field mismatch |
| Downtime API | Frontend | Can list and create downtime reports |
| Field Mapping | Backend | Frontend field names work in requests |
| Test Cases | QA | 45+ test cases documented and executed |

### Sprint 2 Deliverables

| Deliverable | Owner | Acceptance Criteria |
|-------------|-------|---------------------|
| Status Actions | Frontend | Can start/complete/cancel work orders |
| Edit Fix | Frontend | Edit mode loads existing data correctly |
| Search | Frontend | Can filter by work order no, batch, status |
| Test Data | DBA | 100+ varied test records available |

### Sprint 3 Deliverables

| Deliverable | Owner | Acceptance Criteria |
|-------------|-------|---------------------|
| Login Page | Frontend | Users can login with credentials |
| JWT Auth | Backend | All APIs require valid token |
| E2E Tests | QA | Automated tests cover critical paths |

---

## 4. Quick Wins

These issues can be fixed quickly with minimal effort:

| # | Issue | File | Fix | Effort |
|---|-------|------|-----|--------|
| 1 | Delete shows "undefined" | `WorkOrderList.vue:178` | Change `row.work_order_number` to `row.workOrderNo` | ⭐ 5min |
| 2 | Operator list is static | `workorder.js:getOperatorList` | Call `/api/users?role=operator` | ⭐ 15min |
| 3 | Status filter not working | `WorkOrderList.vue` | Add status to API params | ⭐ 15min |
| 4 | Console errors on load | `dashboard.js` | Add null checks for API responses | ⭐ 10min |
| 5 | Missing loading states | `WorkOrderList.vue` | Add `v-loading` directive | ⭐ 10min |

**Quick Fix Code Snippets:**

```javascript
// Fix #1: WorkOrderList.vue line 178
// Before:
`Are you sure you want to delete work order "${row.work_order_number}"?`
// After:
`Are you sure you want to delete work order "${row.workOrderNo || row.id}"?`

// Fix #2: workorder.js - Replace static data
export const getOperatorList = () => {
  return request({
    url: '/users',
    method: 'GET',
    params: { role: 'OPERATOR' }
  })
}

// Fix #3: WorkOrderList.vue - Add status to search
const fetchWorkOrders = async () => {
  const params = {
    pageNum: pagination.page,
    pageSize: pagination.pageSize,
    workOrderNo: searchForm.workOrderNo || undefined,
    batchNo: searchForm.batchNo || undefined,
    status: searchForm.status || undefined  // Add this
  }
  // ...
}
```

---

## 5. Technical Debt

| Category | Issue | Impact | Recommended Solution | Priority |
|----------|-------|--------|---------------------|----------|
| Architecture | Field naming inconsistency | Integration failures | Unified naming + adapter layer | 🔴 High |
| Security | No authentication | Anyone can access | Add JWT authentication | 🔴 High |
| Performance | No caching | Repeated DB queries | Add Redis cache | 🟡 Medium |
| Maintainability | No unit tests | High refactor risk | Add test coverage | 🟡 Medium |
| Observability | No logging standard | Hard to debug | Unified log format | 🟢 Low |
| Code Quality | Hardcoded values | Hard to configure | Use environment variables | 🟢 Low |

---

## 6. Test Data Strategy

### 6.1 Current Test Data

| Table | Records | Coverage |
|-------|---------|----------|
| work_order | 10 | All statuses covered |
| equipment | 7 | All types covered |
| product | 5 | Basic semiconductor products |
| downtime_report | 8 | Various downtime types |
| user | 7 | Multiple roles |

### 6.2 Required Additional Data

| Scenario | Purpose | Records Needed |
|----------|---------|----------------|
| Pagination Test | Verify page navigation | 100+ work orders |
| Search Test | Verify filter accuracy | Various patterns |
| Boundary Test | Verify edge cases | Null, empty, max values |
| Performance Test | Verify response times | 10,000+ records |

### 6.3 Test Data Generation Script

```bash
# Location: scripts/generate-test-data.sql

# Run in MySQL:
# mysql -u root -p smartmes_lite < scripts/generate-test-data.sql
```

---

## 7. Next Steps

### Immediate Actions (Today)

- [ ] Fix delete confirmation "undefined" issue ✅ (Done)
- [ ] Create `src/api/downtime.js` file
- [ ] Fix edit mode data loading

### This Week

- [ ] Complete data adapter implementation
- [ ] Add `@JsonProperty` annotations to backend entities
- [ ] Finish downtime module integration
- [ ] Execute functional test cases

### This Month

- [ ] All CRUD operations working correctly
- [ ] Add basic user authentication
- [ ] Complete core feature test coverage
- [ ] Deploy to staging environment

---

## Appendix

### A. File Structure Reference

```
smartmes-frontend/
├── src/
│   ├── api/
│   │   ├── config.js           # API configuration
│   │   ├── request.js          # Axios instance
│   │   ├── workorder.js        # Work order API
│   │   ├── dashboard.js        # Dashboard API
│   │   └── downtime.js         # [NEW] Downtime API
│   ├── utils/
│   │   └── dataAdapter.js      # [NEW] Data adapter
│   ├── views/
│   │   ├── WorkOrder/
│   │   ├── Dashboard/
│   │   ├── Downtime/
│   │   └── Login.vue           # [NEW] Login page
│   └── store/
│       └── user.js             # [NEW] User state

smartmes-backend/
├── src/main/java/com/smartmes/
│   ├── controller/
│   │   ├── WorkOrderController.java
│   │   ├── DashboardController.java
│   │   ├── BaseDataController.java
│   │   ├── DowntimeController.java   # Enhanced
│   │   ├── UserController.java       # [NEW]
│   │   └── AuthController.java       # [NEW]
│   ├── config/
│   │   └── SecurityConfig.java       # [NEW]
│   └── util/
│       └── JwtTokenProvider.java     # [NEW]
```

### B. API Endpoint Reference

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | /api/workorders | List work orders | ✅ |
| POST | /api/workorders | Create work order | ✅ |
| GET | /api/workorders/{id} | Get work order | ✅ |
| PUT | /api/workorders/{id} | Update work order | ✅ |
| DELETE | /api/workorders/{id} | Delete work order | ✅ |
| PUT | /api/workorders/{id}/start | Start work order | ✅ |
| PUT | /api/workorders/{id}/complete | Complete work order | ✅ |
| PUT | /api/workorders/{id}/cancel | Cancel work order | ✅ |
| GET | /api/dashboard/overview | Production overview | ✅ |
| GET | /api/dashboard/equipment-status | Equipment status | ✅ |
| GET | /api/base-data/product | Product list | ✅ |
| GET | /api/base-data/equipment | Equipment list | ✅ |
| GET | /api/downtime/reports | List downtime | ✅ |
| POST | /api/downtime/report | Create downtime | ✅ |
| POST | /api/auth/login | User login | 🆕 |
| POST | /api/auth/logout | User logout | 🆕 |
| GET | /api/users | List users | 🆕 |

---

*Document generated: 2025-01-09*
*Last updated: 2025-01-09*
