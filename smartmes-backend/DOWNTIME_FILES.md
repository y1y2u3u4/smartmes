# SmartMES Lite - Downtime Module Files

## 已创建文件清单 (Created Files List)

### 1. 枚举类 (Enums)
- `src/main/java/com/smartmes/enums/DowntimeType.java`
  - 异常停机类型枚举
  - Types: EQUIPMENT_FAILURE, MATERIAL_SHORTAGE, QUALITY_ISSUE, OPERATOR_ERROR, OTHER

- `src/main/java/com/smartmes/enums/DowntimeStatus.java`
  - 异常状态枚举
  - Status: PENDING, PROCESSING, RESOLVED

### 2. 实体类 (Entity)
- `src/main/java/com/smartmes/entity/DowntimeReport.java`
  - 异常停机上报实体类
  - 包含所有数据库字段和业务方法
  - 自动计算停机时长功能

### 3. 数据传输对象 (DTOs)
- `src/main/java/com/smartmes/dto/DowntimeReportDTO.java`
  - 创建异常上报的数据传输对象
  - 包含JSR-303验证注解

- `src/main/java/com/smartmes/dto/DowntimeQueryDTO.java`
  - 查询条件DTO
  - 支持多条件筛选和分页

- `src/main/java/com/smartmes/dto/DowntimeRespondDTO.java`
  - 响应异常的数据传输对象

- `src/main/java/com/smartmes/dto/DowntimeResolveDTO.java`
  - 解决异常的数据传输对象

- `src/main/java/com/smartmes/dto/DowntimeStatisticsDTO.java`
  - 统计数据传输对象
  - 包含各类统计信息和TOP5设备

### 4. Mapper层 (MyBatis)
- `src/main/java/com/smartmes/mapper/DowntimeMapper.java`
  - MyBatis Mapper接口
  - 定义所有数据库操作方法

- `src/main/java/com/smartmes/mapper/xml/DowntimeMapper.xml`
  - MyBatis XML映射文件
  - 实现所有SQL查询和更新语句

### 5. 服务层 (Service)
- `src/main/java/com/smartmes/service/DowntimeService.java`
  - 业务逻辑接口
  - 定义7个核心业务方法

- `src/main/java/com/smartmes/service/impl/DowntimeServiceImpl.java`
  - 业务逻辑实现类
  - 包含完整的异常处理和事务管理

### 6. 控制器层 (Controller)
- `src/main/java/com/smartmes/controller/DowntimeController.java`
  - REST API控制器
  - 实现7个API端点

### 7. 通用类 (Common)
- `src/main/java/com/smartmes/common/ApiResponse.java`
  - 统一API响应对象 (已存在，确认兼容)

- `src/main/java/com/smartmes/common/PageResult.java`
  - 分页结果对象 (已存在，确认兼容)

### 8. 配置文件 (Configuration)
- `src/main/resources/application.yml`
  - 应用配置文件
  - 已更新支持MyBatis配置

- `pom.xml`
  - Maven依赖配置
  - 已添加MyBatis和相关依赖

### 9. 数据库脚本 (Database)
- `src/main/resources/db-downtime-report.sql`
  - 数据库建表脚本
  - 包含索引和示例数据

### 10. 主应用类 (Application)
- `src/main/java/com/smartmes/SmartMesApplication.java`
  - Spring Boot主应用类
  - 已添加@MapperScan注解

### 11. 文档 (Documentation)
- `DOWNTIME_MODULE.md`
  - 模块完整文档
  - 包含API使用说明和示例

- `DOWNTIME_FILES.md`
  - 本文件：文件清单

## API端点总览 (API Endpoints)

1. **POST** `/api/downtime/report` - 上报异常停机
2. **GET** `/api/downtime/reports` - 查询异常列表
3. **GET** `/api/downtime/reports/{reportId}` - 查询异常详情
4. **PUT** `/api/downtime/reports/{reportId}/respond` - 响应异常
5. **PUT** `/api/downtime/reports/{reportId}/resolve` - 解决异常
6. **GET** `/api/downtime/statistics` - 获取统计数据
7. **DELETE** `/api/downtime/reports/{reportId}` - 删除异常记录

## 核心功能特性 (Key Features)

1. ✅ 异常停机上报
2. ✅ 多条件查询和分页
3. ✅ 异常响应流程
4. ✅ 异常解决流程
5. ✅ 停机时长自动计算
6. ✅ 综合统计分析
7. ✅ 设备故障TOP5排行
8. ✅ 按类型统计分布
9. ✅ 状态流转管理
10. ✅ 数据验证

## 技术栈 (Tech Stack)

- Spring Boot 3.2.0
- MyBatis 3.0.3
- MySQL 8.0
- Lombok
- Jakarta Validation
- SpringDoc OpenAPI

## 数据库表结构 (Database Schema)

```
downtime_report
├── report_id (BIGINT, PK, AUTO_INCREMENT)
├── order_id (VARCHAR(50))
├── equipment_id (VARCHAR(50))
├── downtime_type (VARCHAR(30))
├── description (TEXT)
├── start_time (DATETIME)
├── end_time (DATETIME)
├── duration_minutes (INT)
├── reporter_id (VARCHAR(50))
├── responder_id (VARCHAR(50))
├── solution (TEXT)
├── status (VARCHAR(20))
├── attachments (TEXT)
├── created_at (DATETIME)
└── updated_at (DATETIME)

索引：
- idx_order_id
- idx_equipment_id
- idx_downtime_type
- idx_status
- idx_start_time
- idx_reporter_id
- idx_created_at
```

## 依赖关系 (Dependencies)

```
Controller → Service → Mapper → Database
    ↓          ↓
   DTO      Entity
```

## 下一步 (Next Steps)

1. ✅ 数据库初始化
   ```bash
   mysql -u root -p smartmes_lite < src/main/resources/db-downtime-report.sql
   ```

2. ✅ 配置数据库连接
   - 修改 `application.yml` 中的数据库连接信息

3. ✅ 运行应用
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. ✅ 测试API
   - 访问 Swagger UI: http://localhost:8080/swagger-ui.html
   - 使用 Postman 或 curl 测试端点

## 注意事项 (Notes)

- 所有时间字段使用 `LocalDateTime` 类型
- 停机时长自动计算（分钟为单位）
- 枚举类型使用 `EnumTypeHandler` 处理
- API响应格式统一为 `ApiResponse<T>`
- 分页查询使用 `PageResult<T>` 封装
- 所有API已添加详细的日志记录
- 事务管理使用 `@Transactional` 注解
