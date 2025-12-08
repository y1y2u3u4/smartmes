# SmartMES Lite - Downtime Report Module

## Overview

SmartMES Lite异常停机上报模块后端实现，提供完整的REST API用于生产异常管理。

## Module Structure

```
smartmes-backend/
├── src/main/java/com/smartmes/
│   ├── entity/
│   │   └── DowntimeReport.java           # Downtime report entity
│   ├── dto/
│   │   ├── DowntimeReportDTO.java        # Create report DTO
│   │   ├── DowntimeQueryDTO.java         # Query filter DTO
│   │   ├── DowntimeRespondDTO.java       # Respond to report DTO
│   │   ├── DowntimeResolveDTO.java       # Resolve report DTO
│   │   └── DowntimeStatisticsDTO.java    # Statistics DTO
│   ├── enums/
│   │   ├── DowntimeType.java             # Downtime type enum
│   │   └── DowntimeStatus.java           # Report status enum
│   ├── mapper/
│   │   ├── DowntimeMapper.java           # MyBatis mapper interface
│   │   └── xml/
│   │       └── DowntimeMapper.xml        # MyBatis XML mapping
│   ├── service/
│   │   ├── DowntimeService.java          # Service interface
│   │   └── impl/
│   │       └── DowntimeServiceImpl.java  # Service implementation
│   └── controller/
│       └── DowntimeController.java       # REST API controller
└── src/main/resources/
    └── db-downtime-report.sql           # Database schema
```

## API Endpoints

### 1. Report Downtime
**POST** `/api/downtime/report`

Create a new downtime report.

**Request Body:**
```json
{
  "orderId": "WO-20240101-001",
  "equipmentId": "EQ-001",
  "downtimeType": "EQUIPMENT_FAILURE",
  "description": "Conveyor belt motor malfunction",
  "startTime": "2024-01-15T09:30:00",
  "endTime": "2024-01-15T11:00:00",
  "reporterId": "USER-001",
  "attachments": "/uploads/photo1.jpg,/uploads/photo2.jpg"
}
```

### 2. Query Downtime Reports
**GET** `/api/downtime/reports?equipmentId=EQ-001&status=PENDING&pageNum=1&pageSize=10`

Query downtime reports with pagination and filters.

### 3. Get Report Details
**GET** `/api/downtime/reports/{reportId}`

Get detailed information of a specific report.

### 4. Respond to Downtime
**PUT** `/api/downtime/reports/{reportId}/respond`

Mark a report as being processed.

### 5. Resolve Downtime
**PUT** `/api/downtime/reports/{reportId}/resolve`

Mark a report as resolved with solution details.

### 6. Get Statistics
**GET** `/api/downtime/statistics`

Get comprehensive downtime statistics.

### 7. Delete Report
**DELETE** `/api/downtime/reports/{reportId}`

Delete a downtime report.

## Downtime Types

- `EQUIPMENT_FAILURE` - Equipment failure or malfunction
- `MATERIAL_SHORTAGE` - Insufficient raw materials or components
- `QUALITY_ISSUE` - Product quality problem
- `OPERATOR_ERROR` - Human error during operation
- `OTHER` - Other types of downtime

## Report Status Workflow

```
PENDING (待处理)
    ↓ respond
PROCESSING (处理中)
    ↓ resolve
RESOLVED (已解决)
```

## Database Schema

```sql
CREATE TABLE downtime_report (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(50) NOT NULL,
    equipment_id VARCHAR(50) NOT NULL,
    downtime_type VARCHAR(30) NOT NULL,
    description TEXT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME DEFAULT NULL,
    duration_minutes INT DEFAULT NULL,
    reporter_id VARCHAR(50) NOT NULL,
    responder_id VARCHAR(50) DEFAULT NULL,
    solution TEXT DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attachments TEXT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## Key Features

1. **Automatic Duration Calculation**: System automatically calculates downtime duration when end time is provided
2. **Status Workflow**: Reports follow a defined workflow (PENDING → PROCESSING → RESOLVED)
3. **Comprehensive Statistics**: Real-time statistics including type distribution and equipment rankings
4. **Flexible Querying**: Support for multiple filters and sorting options
5. **Pagination Support**: Efficient handling of large datasets
6. **Data Validation**: Input validation using JSR-303 annotations
7. **Error Handling**: Consistent error responses across all endpoints

## Integration with Order Module

The downtime report module is associated with the work order module through the `orderId` field:

```java
private String orderId;  // Links to work order
```

This allows:
- Tracking which production orders were affected
- Analyzing downtime impact on order completion
- Generating order-specific downtime reports

## Usage Example

```java
// 1. Create a downtime report
DowntimeReportDTO dto = DowntimeReportDTO.builder()
    .orderId("WO-20240101-001")
    .equipmentId("EQ-001")
    .downtimeType(DowntimeType.EQUIPMENT_FAILURE)
    .description("Motor malfunction")
    .startTime(LocalDateTime.now())
    .reporterId("USER-001")
    .build();

// 2. Submit via API
POST /api/downtime/report

// 3. Respond to the report
PUT /api/downtime/reports/1/respond
{
  "responderId": "TECH-001",
  "responseNotes": "On-site inspection"
}

// 4. Resolve the issue
PUT /api/downtime/reports/1/resolve
{
  "endTime": "2024-01-15T11:00:00",
  "solution": "Replaced motor"
}

// 5. View statistics
GET /api/downtime/statistics
```

## Testing

Access Swagger UI for interactive API testing:
```
http://localhost:8080/swagger-ui.html
```

## Notes

- All timestamps use ISO 8601 format
- Duration is automatically calculated in minutes
- Attachments are stored as comma-separated file paths
- API base path is `/api` (configured in application.yml)
