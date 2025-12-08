# SmartMES Lite Backend - Quick Start Guide

## 快速开始指南

本指南帮助您快速启动和测试 SmartMES Lite 后端服务。

---

## 1. 环境要求

确保已安装以下软件：

- ✅ Java 17 或更高版本
- ✅ Maven 3.6 或更高版本
- ✅ MySQL 8.0 或更高版本
- ✅ 终端或命令行工具

### 验证环境

```bash
# 检查 Java 版本
java -version
# 应输出: openjdk version "17.x.x" 或更高

# 检查 Maven 版本
mvn -version
# 应输出: Apache Maven 3.6.x 或更高

# 检查 MySQL
mysql --version
# 应输出: mysql Ver 8.0.x
```

---

## 2. 数据库设置 (5分钟)

### 步骤 1: 创建数据库

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE smartmes_lite CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 退出
exit;
```

### 步骤 2: 导入测试数据

```bash
# 进入项目目录
cd /Users/zhanggongqing/project/孵化项目/test_demo

# 导入数据
mysql -u root -p smartmes_lite < database/init_smartmes.sql
```

**测试数据包含**:
- 10 台设备（不同状态）
- 10 个产品
- 10 个工单（今日和昨日）
- 8 条异常记录

---

## 3. 配置应用 (2分钟)

### 编辑数据库连接

打开 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smartmes_lite?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root        # 修改为你的MySQL用户名
    password: root        # 修改为你的MySQL密码
```

---

## 4. 构建和运行 (5分钟)

### 方式 1: 使用 Maven 直接运行（推荐）

```bash
# 进入后端目录
cd smartmes-backend

# 构建并运行
mvn spring-boot:run
```

### 方式 2: 打包后运行

```bash
# 构建项目
mvn clean package

# 运行 JAR 文件
java -jar target/smartmes-backend-1.0.0.jar
```

### 成功标志

看到以下输出表示启动成功：

```
===========================================
SmartMES Lite Backend Started Successfully
API Base URL: http://localhost:8080/api
===========================================
```

---

## 5. 测试 API (5分钟)

### 使用浏览器测试

在浏览器中访问：

```
http://localhost:8080/api/dashboard/overview
```

应该看到类似以下的 JSON 响应：

```json
{
  "code": 200,
  "message": "Success",
  "data": {
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
}
```

### 使用 cURL 测试

```bash
# 测试生产概览
curl http://localhost:8080/api/dashboard/overview

# 测试异常统计
curl http://localhost:8080/api/dashboard/downtime-stats

# 测试工单进度
curl http://localhost:8080/api/dashboard/workorder-progress

# 测试设备状态
curl http://localhost:8080/api/dashboard/equipment-status

# 获取所有设备
curl http://localhost:8080/api/base-data/equipment

# 获取所有产品
curl http://localhost:8080/api/base-data/product
```

### 使用 Postman 测试

1. 打开 Postman
2. 创建新请求: GET `http://localhost:8080/api/dashboard/overview`
3. 点击 Send
4. 查看响应

---

## 6. 常见问题

### Q1: 数据库连接失败

**错误**: `Access denied for user 'root'@'localhost'`

**解决**:
- 检查 `application.yml` 中的用户名和密码
- 确认 MySQL 服务正在运行
- 尝试直接连接: `mysql -u root -p`

### Q2: 端口 8080 被占用

**错误**: `Port 8080 is already in use`

**解决**:
在 `application.yml` 中修改端口:
```yaml
server:
  port: 8081  # 改为其他端口
```

### Q3: 找不到数据库

**错误**: `Unknown database 'smartmes_lite'`

**解决**:
重新创建数据库:
```sql
CREATE DATABASE smartmes_lite CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Q4: Java 版本不匹配

**错误**: `unsupported class file version`

**解决**:
- 确保使用 Java 17 或更高版本
- 运行 `java -version` 验证版本

---

## 7. 完整测试流程

### 测试看板数据

```bash
# 1. 获取生产概览
curl http://localhost:8080/api/dashboard/overview | jq

# 2. 获取异常统计
curl http://localhost:8080/api/dashboard/downtime-stats | jq

# 3. 获取工单进度
curl http://localhost:8080/api/dashboard/workorder-progress | jq

# 4. 获取设备状态
curl http://localhost:8080/api/dashboard/equipment-status | jq

# 5. 获取完整看板数据
curl http://localhost:8080/api/dashboard/complete | jq
```

> 注: `jq` 是 JSON 格式化工具，如未安装可以省略 `| jq`

### 测试基础数据管理

```bash
# 创建新设备
curl -X POST http://localhost:8080/api/base-data/equipment \
  -H "Content-Type: application/json" \
  -d '{
    "equipmentId": "EQ011",
    "equipmentName": "Test Machine",
    "equipmentType": "Test",
    "lineId": "LINE01",
    "status": "IDLE",
    "location": "Test Area"
  }'

# 获取所有设备
curl http://localhost:8080/api/base-data/equipment

# 创建新产品
curl -X POST http://localhost:8080/api/base-data/product \
  -H "Content-Type: application/json" \
  -d '{
    "productCode": "PROD011",
    "productName": "Test Product",
    "productType": "Test",
    "unit": "pcs",
    "standardWorkTime": 20,
    "status": "ACTIVE"
  }'

# 获取所有产品
curl http://localhost:8080/api/base-data/product
```

---

## 8. 下一步

现在您已经成功运行了 SmartMES Lite 后端，可以：

1. **查看 API 文档**: 阅读 `docs/API_Documentation.md`
2. **了解架构**: 阅读 `PROJECT_SUMMARY.md`
3. **开发前端**: 连接到后端 API 开发前端界面
4. **扩展功能**: 添加新的业务逻辑和 API

---

## 9. 停止应用

### 方式 1: 如果使用 mvn spring-boot:run
在终端按 `Ctrl + C`

### 方式 2: 如果后台运行
```bash
# 查找进程
ps aux | grep smartmes

# 终止进程
kill <PID>
```

---

## 10. 开发模式

### 启用热重载

```bash
# 使用 spring-boot-devtools 实现热重载
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### 查看日志

应用日志会输出到控制台，也可以配置输出到文件：

在 `application.yml` 中添加:
```yaml
logging:
  file:
    name: logs/smartmes.log
  level:
    com.smartmes: DEBUG
```

---

## 总结

完成以上步骤后，您应该能够：

- ✅ 成功启动 SmartMES Lite 后端服务
- ✅ 访问所有 API 端点
- ✅ 看到测试数据的响应
- ✅ 使用浏览器、cURL 或 Postman 测试 API
- ✅ 理解基本的项目结构

**预计总时间**: 15-20 分钟

如有问题，请查看:
- `README.md` - 详细文档
- `PROJECT_SUMMARY.md` - 架构说明
- `docs/API_Documentation.md` - API 参考

---

**祝您使用愉快！**

SmartMES Team  
2024年12月8日
