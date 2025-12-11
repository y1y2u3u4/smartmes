# SmartMES Lite API Documentation

## API Overview

SmartMES Lite provides RESTful APIs for manufacturing execution system operations. All APIs return data in JSON format with a unified response structure.

## Base URL

```
http://localhost:8080/api
```

## Response Format

### Success Response

```json
{
  "code": 200,
  "message": "Success",
  "data": { ... }
}
```

### Error Response

```json
{
  "code": 500,
  "message": "Error message description",
  "data": null
}
```

## API Endpoints

### 1. Dashboard APIs

#### 1.1 Get Production Overview

Get today's production overview including work orders, output, and equipment status.

**Endpoint:** `GET /dashboard/overview`

**Response Data:**
```json
{
  "todayWorkOrderTotal": 8,
  "todayCompleted": 2,
  "todayInProgress": 3,
  "todayAbnormal": 1,
  "planQtyTotal": 5300,
  "actualQtyTotal": 2500,
  "completionRate": 47.17,
  "equipmentRunning": 5,
  "equipmentIdle": 2,
  "equipmentFault": 1
}
```

#### 1.2 Get Downtime Statistics

Get today's downtime and exception statistics.

**Endpoint:** `GET /dashboard/downtime-stats`

**Response Data:**
```json
{
  "todayDowntimeCount": 6,
  "totalDowntimeMinutes": 120,
  "downtimeByType": {
    "Equipment Fault": 2,
    "Quality Issue": 1,
    "Material Shortage": 1,
    "Tool Change": 1,
    "Other": 1
  },
  "topFaultyEquipment": [
    {
      "equipmentCode": "EQ001",
      "equipmentName": "Injection Molding Machine 1",
      "faultCount": 2,
      "totalDowntimeMinutes": 60
    }
  ]
}
```

#### 1.3 Get Work Order Progress

Get today's work order progress information.

**Endpoint:** `GET /dashboard/workorder-progress`

**Response Data:**
```json
{
  "workOrders": [
    {
      "workOrderNo": "WO20241208001",
      "productCode": "PROD001",
      "productName": "Plastic Housing Model A",
      "lineId": "LINE01",
      "status": "COMPLETED",
      "statusName": "Completed",
      "planQty": 1000,
      "actualQty": 1000,
      "completionRate": 100.0,
      "planStartTime": "2024-12-08T08:00:00",
      "planEndTime": "2024-12-08T12:00:00",
      "actualStartTime": "2024-12-08T08:00:00",
      "actualEndTime": "2024-12-08T11:45:00",
      "priority": 5
    }
  ]
}
```

#### 1.4 Get Equipment Status

Get current status of all equipment.

**Endpoint:** `GET /dashboard/equipment-status`

**Response Data:**
```json
{
  "equipmentList": [
    {
      "equipmentId": "EQ001",
      "equipmentName": "Injection Molding Machine 1",
      "equipmentType": "Injection Molding",
      "lineId": "LINE01",
      "status": "RUNNING",
      "statusName": "Running",
      "location": "Workshop A - Area 1",
      "lastMaintenanceTime": "2024-11-15T08:00:00",
      "nextMaintenanceTime": "2024-12-15T08:00:00"
    }
  ],
  "runningCount": 5,
  "idleCount": 2,
  "maintenanceCount": 1,
  "faultCount": 1
}
```

#### 1.5 Get Complete Dashboard Data

Get all dashboard data in one request.

**Endpoint:** `GET /dashboard/complete`

**Response:** Combines all above data into one response.

---

### 2. Equipment Management APIs

#### 2.1 Create Equipment

**Endpoint:** `POST /base-data/equipment`

**Request Body:**
```json
{
  "equipmentId": "EQ011",
  "equipmentName": "New Machine",
  "equipmentType": "Injection Molding",
  "lineId": "LINE01",
  "status": "IDLE",
  "location": "Workshop A - Area 1",
  "lastMaintenanceTime": "2024-11-01T08:00:00",
  "nextMaintenanceTime": "2024-12-01T08:00:00",
  "remarks": "New equipment"
}
```

#### 2.2 Update Equipment

**Endpoint:** `PUT /base-data/equipment/{id}`

**Request Body:** Same as Create Equipment

#### 2.3 Delete Equipment

**Endpoint:** `DELETE /base-data/equipment/{id}`

#### 2.4 Get Equipment Details

**Endpoint:** `GET /base-data/equipment/{id}`

#### 2.5 Get All Equipment

**Endpoint:** `GET /base-data/equipment`

#### 2.6 Get Equipment by Production Line

**Endpoint:** `GET /base-data/equipment/by-line/{lineId}`

---

### 3. Product Management APIs

#### 3.1 Create Product

**Endpoint:** `POST /base-data/product`

**Request Body:**
```json
{
  "productCode": "PROD011",
  "productName": "New Product",
  "specification": "Size: 100x100x50mm",
  "productType": "Plastic Parts",
  "unit": "pcs",
  "standardWorkTime": 20,
  "status": "ACTIVE",
  "remarks": "New product line"
}
```

#### 3.2 Update Product

**Endpoint:** `PUT /base-data/product/{id}`

**Request Body:** Same as Create Product

#### 3.3 Delete Product

**Endpoint:** `DELETE /base-data/product/{id}`

#### 3.4 Get Product Details

**Endpoint:** `GET /base-data/product/{id}`

#### 3.5 Get All Products

**Endpoint:** `GET /base-data/product`

#### 3.6 Search Products by Name

**Endpoint:** `GET /base-data/product/search?name={keyword}`

---

## Data Models

### Equipment Status Enum

- `RUNNING` - Running
- `IDLE` - Idle
- `MAINTENANCE` - Under maintenance
- `FAULT` - Faulty

### Work Order Status Enum

- `PENDING` - Pending
- `IN_PROGRESS` - In progress
- `COMPLETED` - Completed
- `ABNORMAL` - Abnormal
- `CANCELLED` - Cancelled

### Downtime Type Enum

- `EQUIPMENT_FAULT` - Equipment fault
- `QUALITY_ISSUE` - Quality issue
- `MATERIAL_SHORTAGE` - Material shortage
- `TOOL_CHANGE` - Tool change
- `OTHER` - Other

### Product Status Enum

- `ACTIVE` - Active
- `INACTIVE` - Inactive

---

## Error Codes

| Code | Description |
|------|-------------|
| 200  | Success |
| 400  | Bad Request - Invalid parameters |
| 404  | Not Found - Resource does not exist |
| 500  | Internal Server Error |

---

## Testing with cURL

### Get Production Overview
```bash
curl -X GET http://localhost:8080/api/dashboard/overview
```

### Create Equipment
```bash
curl -X POST http://localhost:8080/api/base-data/equipment \
  -H "Content-Type: application/json" \
  -d '{
    "equipmentId": "EQ011",
    "equipmentName": "New Machine",
    "equipmentType": "Injection Molding",
    "lineId": "LINE01",
    "status": "IDLE"
  }'
```

### Get All Products
```bash
curl -X GET http://localhost:8080/api/base-data/product
```

---

## Notes

1. All timestamps are in ISO 8601 format: `YYYY-MM-DDTHH:mm:ss`
2. All APIs support CORS for cross-origin requests
3. The dashboard APIs aggregate data based on today's date (server timezone)
4. Equipment and product IDs must be unique within the system
