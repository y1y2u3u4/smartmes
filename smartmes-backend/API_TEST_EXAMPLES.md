# SmartMES Lite API 测试示例

本文档提供了完整的API测试示例，可以使用curl、Postman或其他HTTP客户端进行测试。

## 环境配置

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`

## 1. 工单管理 API (WorkOrder)

### 1.1 创建工单

**请求**:
```bash
curl -X POST http://localhost:8080/api/workorders \
  -H "Content-Type: application/json" \
  -d '{
    "workOrderNo": "WO20231209001",
    "productId": 1,
    "productCode": "P001",
    "productName": "Product A",
    "lineId": "LINE001",
    "planQty": 1000,
    "planStartTime": "2023-12-09 08:00:00",
    "planEndTime": "2023-12-09 18:00:00",
    "priority": 5,
    "remarks": "Test work order creation"
  }'
```

**响应**:
```json
{
  "code": 200,
  "message": "Work order created successfully",
  "data": {
    "id": 6,
    "workOrderNo": "WO20231209001",
    "productId": 1,
    "productCode": "P001",
    "productName": "Product A",
    "lineId": "LINE001",
    "planQty": 1000,
    "actualQty": 0,
    "qualifiedQty": 0,
    "defectQty": 0,
    "status": "PENDING",
    "planStartTime": "2023-12-09T08:00:00",
    "planEndTime": "2023-12-09T18:00:00",
    "actualStartTime": null,
    "actualEndTime": null,
    "priority": 5,
    "createTime": "2023-12-08T19:30:00",
    "updateTime": "2023-12-08T19:30:00",
    "remarks": "Test work order creation"
  },
  "timestamp": 1702037400000
}
```

### 1.2 查询工单列表（分页）

**请求**:
```bash
curl "http://localhost:8080/api/workorders?pageNum=1&pageSize=10&sortBy=createTime&sortDir=DESC"
```

**响应**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "records": [
      {
        "id": 6,
        "workOrderNo": "WO20231209001",
        "productCode": "P001",
        "status": "PENDING",
        ...
      }
    ],
    "total": 6,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 1
  },
  "timestamp": 1702037400000
}
```

### 1.3 查询工单详情

**请求**:
```bash
curl http://localhost:8080/api/workorders/1
```

**响应**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "workOrderNo": "WO20231208001",
    "productId": 1,
    "productCode": "P001",
    "productName": "Product A",
    "lineId": "LINE001",
    "planQty": 1000,
    "actualQty": 0,
    "qualifiedQty": 0,
    "defectQty": 0,
    "status": "PENDING",
    ...
  },
  "timestamp": 1702037400000
}
```

### 1.4 根据工单号查询

**请求**:
```bash
curl http://localhost:8080/api/workorders/no/WO20231208001
```

### 1.5 更新工单信息

**请求**:
```bash
curl -X PUT http://localhost:8080/api/workorders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "planQty": 1200,
    "priority": 4,
    "remarks": "Updated plan quantity"
  }'
```

**响应**:
```json
{
  "code": 200,
  "message": "Work order updated successfully",
  "data": {
    "id": 1,
    "planQty": 1200,
    "priority": 4,
    "remarks": "Updated plan quantity",
    ...
  },
  "timestamp": 1702037400000
}
```

### 1.6 开始工单

**请求**:
```bash
curl -X PUT http://localhost:8080/api/workorders/1/start
```

**响应**:
```json
{
  "code": 200,
  "message": "Work order started successfully",
  "data": {
    "id": 1,
    "status": "IN_PROGRESS",
    "actualStartTime": "2023-12-08T19:35:00",
    ...
  },
  "timestamp": 1702037700000
}
```

### 1.7 更新工单进度

**请求**:
```bash
curl -X PUT http://localhost:8080/api/workorders/1/progress \
  -H "Content-Type: application/json" \
  -d '{
    "actualQty": 500,
    "qualifiedQty": 495,
    "defectQty": 5
  }'
```

**响应**:
```json
{
  "code": 200,
  "message": "Work order progress updated successfully",
  "data": {
    "id": 1,
    "actualQty": 500,
    "qualifiedQty": 495,
    "defectQty": 5,
    "completionRate": 50.0,
    ...
  },
  "timestamp": 1702037800000
}
```

### 1.8 完成工单

**请求**:
```bash
curl -X PUT http://localhost:8080/api/workorders/1/complete \
  -H "Content-Type: application/json" \
  -d '{
    "actualQty": 1000,
    "qualifiedQty": 985,
    "defectQty": 15
  }'
```

**响应**:
```json
{
  "code": 200,
  "message": "Work order completed successfully",
  "data": {
    "id": 1,
    "status": "COMPLETED",
    "actualQty": 1000,
    "qualifiedQty": 985,
    "defectQty": 15,
    "actualEndTime": "2023-12-08T19:40:00",
    ...
  },
  "timestamp": 1702038000000
}
```

### 1.9 标记工单异常

**请求**:
```bash
curl -X PUT http://localhost:8080/api/workorders/2/abnormal \
  -H "Content-Type: application/json" \
  -d '{
    "remarks": "Equipment failure - production interrupted"
  }'
```

**响应**:
```json
{
  "code": 200,
  "message": "Work order marked as abnormal successfully",
  "data": {
    "id": 2,
    "status": "ABNORMAL",
    "remarks": "Equipment failure - production interrupted",
    ...
  },
  "timestamp": 1702038100000
}
```

### 1.10 取消工单

**请求**:
```bash
curl -X PUT http://localhost:8080/api/workorders/3/cancel
```

**响应**:
```json
{
  "code": 200,
  "message": "Work order cancelled successfully",
  "data": {
    "id": 3,
    "status": "CANCELLED",
    ...
  },
  "timestamp": 1702038200000
}
```

### 1.11 条件查询工单

**请求**:
```bash
# 查询产品P001的进行中工单
curl "http://localhost:8080/api/workorders/search?productCode=P001&status=IN_PROGRESS&pageNum=1&pageSize=10"

# 查询产线LINE001的所有工单
curl "http://localhost:8080/api/workorders/search?lineId=LINE001&pageNum=1&pageSize=10"

# 查询时间范围内的工单
curl "http://localhost:8080/api/workorders/search?startTime=2023-12-08%2000:00:00&endTime=2023-12-09%2023:59:59&pageNum=1&pageSize=10"

# 组合查询
curl "http://localhost:8080/api/workorders/search?productCode=P001&status=COMPLETED&lineId=LINE001&pageNum=1&pageSize=10&sortBy=createTime&sortDir=DESC"
```

**响应**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "records": [ ... ],
    "total": 2,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 1
  },
  "timestamp": 1702038300000
}
```

### 1.12 统计工单数量

**请求**:
```bash
# 统计待开始的工单
curl http://localhost:8080/api/workorders/count/PENDING

# 统计进行中的工单
curl http://localhost:8080/api/workorders/count/IN_PROGRESS

# 统计已完成的工单
curl http://localhost:8080/api/workorders/count/COMPLETED
```

**响应**:
```json
{
  "code": 200,
  "message": "Success",
  "data": 3,
  "timestamp": 1702038400000
}
```

### 1.13 查询产线进行中工单

**请求**:
```bash
curl http://localhost:8080/api/workorders/line/LINE001/in-progress
```

**响应**:
```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 2,
      "workOrderNo": "WO20231208002",
      "lineId": "LINE001",
      "status": "IN_PROGRESS",
      ...
    }
  ],
  "timestamp": 1702038500000
}
```

### 1.14 查询逾期工单

**请求**:
```bash
curl "http://localhost:8080/api/workorders/overdue?pageNum=1&pageSize=10"
```

**响应**:
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "records": [
      {
        "id": 5,
        "workOrderNo": "WO20231207001",
        "planEndTime": "2023-12-07T18:00:00",
        "status": "IN_PROGRESS",
        ...
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 1
  },
  "timestamp": 1702038600000
}
```

### 1.15 检查工单号是否存在

**请求**:
```bash
curl http://localhost:8080/api/workorders/exists/WO20231208001
```

**响应**:
```json
{
  "code": 200,
  "message": "Success",
  "data": true,
  "timestamp": 1702038700000
}
```

### 1.16 删除工单

**请求**:
```bash
curl -X DELETE http://localhost:8080/api/workorders/6
```

**响应**:
```json
{
  "code": 200,
  "message": "Work order deleted successfully",
  "data": null,
  "timestamp": 1702038800000
}
```

## 2. 批量操作

### 2.1 批量创建工单

**请求**:
```bash
curl -X POST http://localhost:8080/api/workorders/batch \
  -H "Content-Type: application/json" \
  -d '[
    {
      "workOrderNo": "WO20231210001",
      "productId": 1,
      "productCode": "P001",
      "productName": "Product A",
      "lineId": "LINE001",
      "planQty": 500,
      "planStartTime": "2023-12-10 08:00:00",
      "planEndTime": "2023-12-10 18:00:00",
      "priority": 3
    },
    {
      "workOrderNo": "WO20231210002",
      "productId": 2,
      "productCode": "P002",
      "productName": "Product B",
      "lineId": "LINE002",
      "planQty": 800,
      "planStartTime": "2023-12-10 08:00:00",
      "planEndTime": "2023-12-10 18:00:00",
      "priority": 4
    }
  ]'
```

**响应**:
```json
{
  "code": 200,
  "message": "Work orders created successfully",
  "data": [
    {
      "id": 7,
      "workOrderNo": "WO20231210001",
      ...
    },
    {
      "id": 8,
      "workOrderNo": "WO20231210002",
      ...
    }
  ],
  "timestamp": 1702038900000
}
```

## 3. 错误响应示例

### 3.1 工单不存在

**请求**:
```bash
curl http://localhost:8080/api/workorders/999
```

**响应**:
```json
{
  "code": 404,
  "message": "Work order not found: 999",
  "data": null,
  "timestamp": 1702039000000
}
```

### 3.2 工单号重复

**请求**:
```bash
curl -X POST http://localhost:8080/api/workorders \
  -H "Content-Type: application/json" \
  -d '{
    "workOrderNo": "WO20231208001",
    ...
  }'
```

**响应**:
```json
{
  "code": 400,
  "message": "Work order number already exists: WO20231208001",
  "data": null,
  "timestamp": 1702039100000
}
```

### 3.3 状态不允许操作

**请求**:
```bash
# 尝试开始一个已完成的工单
curl -X PUT http://localhost:8080/api/workorders/3/start
```

**响应**:
```json
{
  "code": 400,
  "message": "Cannot start work order in status: COMPLETED",
  "data": null,
  "timestamp": 1702039200000
}
```

## 4. Postman 集合导入

可以将以下JSON保存为 `SmartMES_API_Collection.json` 并导入到Postman中：

```json
{
  "info": {
    "name": "SmartMES Lite API Collection",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "WorkOrder",
      "item": [
        {
          "name": "Create WorkOrder",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"workOrderNo\": \"WO20231209001\",\n  \"productId\": 1,\n  \"productCode\": \"P001\",\n  \"productName\": \"Product A\",\n  \"lineId\": \"LINE001\",\n  \"planQty\": 1000,\n  \"planStartTime\": \"2023-12-09 08:00:00\",\n  \"planEndTime\": \"2023-12-09 18:00:00\",\n  \"priority\": 5\n}"
            },
            "url": {
              "raw": "http://localhost:8080/api/workorders",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8080",
              "path": ["api", "workorders"]
            }
          }
        }
      ]
    }
  ]
}
```

## 5. 测试场景示例

### 场景1: 完整的工单生命周期

```bash
# 1. 创建工单
curl -X POST http://localhost:8080/api/workorders \
  -H "Content-Type: application/json" \
  -d '{"workOrderNo":"TEST001","productId":1,"productCode":"P001","productName":"Test Product","lineId":"LINE001","planQty":100,"planStartTime":"2023-12-09 08:00:00","planEndTime":"2023-12-09 18:00:00","priority":3}'

# 2. 开始工单
curl -X PUT http://localhost:8080/api/workorders/1/start

# 3. 更新进度（第一次）
curl -X PUT http://localhost:8080/api/workorders/1/progress \
  -H "Content-Type: application/json" \
  -d '{"actualQty":30,"qualifiedQty":30,"defectQty":0}'

# 4. 更新进度（第二次）
curl -X PUT http://localhost:8080/api/workorders/1/progress \
  -H "Content-Type: application/json" \
  -d '{"actualQty":70,"qualifiedQty":68,"defectQty":2}'

# 5. 完成工单
curl -X PUT http://localhost:8080/api/workorders/1/complete \
  -H "Content-Type: application/json" \
  -d '{"actualQty":100,"qualifiedQty":97,"defectQty":3}'

# 6. 查询完成后的工单
curl http://localhost:8080/api/workorders/1
```

### 场景2: 异常处理流程

```bash
# 1. 创建并开始工单
curl -X POST http://localhost:8080/api/workorders \
  -H "Content-Type: application/json" \
  -d '{"workOrderNo":"TEST002","productId":1,"productCode":"P001","productName":"Test Product","lineId":"LINE001","planQty":100,"planStartTime":"2023-12-09 08:00:00","planEndTime":"2023-12-09 18:00:00","priority":3}'

curl -X PUT http://localhost:8080/api/workorders/2/start

# 2. 标记异常
curl -X PUT http://localhost:8080/api/workorders/2/abnormal \
  -H "Content-Type: application/json" \
  -d '{"remarks":"设备故障需要维修"}'

# 3. 查询异常工单
curl "http://localhost:8080/api/workorders/search?status=ABNORMAL"

# 4. 异常解决后完成工单
curl -X PUT http://localhost:8080/api/workorders/2/complete \
  -H "Content-Type: application/json" \
  -d '{"actualQty":80,"qualifiedQty":78,"defectQty":2}'
```

## 6. 性能测试建议

使用Apache Bench或wrk进行压力测试：

```bash
# 使用Apache Bench测试查询接口
ab -n 1000 -c 10 http://localhost:8080/api/workorders

# 使用wrk测试
wrk -t4 -c100 -d30s http://localhost:8080/api/workorders
```

---

**注意事项**:
1. 所有时间格式使用: `yyyy-MM-dd HH:mm:ss`
2. 工单状态: PENDING, IN_PROGRESS, COMPLETED, ABNORMAL, CANCELLED
3. 分页参数pageNum从1开始
4. 排序方向: ASC（升序）或 DESC（降序）

**版本**: 1.0.0
**更新时间**: 2023-12-08
