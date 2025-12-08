# SmartMES Lite Backend 启动指南

## 项目概述

SmartMES Lite 是一个轻量级的制造执行系统后端服务，提供工单管理、生产监控等核心功能。

**项目路径**: `/Users/zhanggongqing/project/孵化项目/test_demo/smartmes-backend`

## 技术栈

- **Java**: JDK 17
- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA (Hibernate)
- **构建工具**: Maven 3.6+

## 前置准备检查清单

### 1. 环境检查

```bash
# 检查 Java 版本（需要 JDK 17+）
java -version

# 检查 Maven 版本（需要 3.6+）
mvn -version

# 检查 MySQL 版本（需要 8.0+）
mysql --version
```

### 2. 数据库准备

#### 步骤1: 启动MySQL服务

```bash
# macOS (使用 Homebrew)
brew services start mysql

# 或手动启动
mysql.server start
```

#### 步骤2: 登录MySQL

```bash
mysql -u root -p
```

#### 步骤3: 执行初始化脚本

```sql
-- 方式1: 在MySQL命令行中执行
source /Users/zhanggongqing/project/孵化项目/test_demo/smartmes-backend/sql/init.sql;

-- 方式2: 使用命令行直接导入
mysql -u root -p < /Users/zhanggongqing/project/孵化项目/test_demo/smartmes-backend/sql/init.sql
```

#### 步骤4: 验证数据库创建

```sql
-- 查看数据库
SHOW DATABASES LIKE 'smartmes_lite';

-- 使用数据库
USE smartmes_lite;

-- 查看表
SHOW TABLES;

-- 查看测试数据
SELECT * FROM work_order;
```

### 3. 配置文件检查

编辑配置文件: `src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smartmes_lite?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root          # 修改为你的MySQL用户名
    password: root          # 修改为你的MySQL密码
```

**重要**: 确保数据库用户名和密码正确！

## 启动步骤

### 方式1: 使用 Maven 命令行启动

```bash
# 进入项目目录
cd /Users/zhanggongqing/project/孵化项目/test_demo/smartmes-backend

# 清理并编译（首次启动）
mvn clean install -DskipTests

# 启动应用
mvn spring-boot:run
```

### 方式2: 使用 IDE 启动（推荐）

#### IntelliJ IDEA:

1. **打开项目**
   - File → Open → 选择 `smartmes-backend` 目录

2. **等待Maven依赖下载**
   - IDEA会自动识别Maven项目并下载依赖
   - 查看右下角进度条

3. **配置JDK**
   - File → Project Structure → Project
   - 设置 Project SDK 为 JDK 17

4. **运行应用**
   - 找到 `SmartMesApplication.java`
   - 右键 → Run 'SmartMesApplication'

#### Eclipse:

1. **导入项目**
   - File → Import → Maven → Existing Maven Projects
   - 选择 `smartmes-backend` 目录

2. **更新Maven项目**
   - 右键项目 → Maven → Update Project

3. **运行应用**
   - 右键 `SmartMesApplication.java`
   - Run As → Java Application

### 方式3: 打包成JAR运行

```bash
# 打包
mvn clean package -DskipTests

# 运行JAR
java -jar target/smartmes-backend-1.0.0.jar
```

## 验证启动成功

### 1. 查看启动日志

启动成功时会看到如下日志：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

...

Started SmartMesApplication in 3.456 seconds
```

### 2. 测试API接口

#### 测试1: 健康检查（如果有）

```bash
curl http://localhost:8080/api/actuator/health
```

#### 测试2: 查询工单列表

```bash
curl http://localhost:8080/api/workorders
```

预期响应：

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "records": [...],
    "total": 5,
    "pageNum": 1,
    "pageSize": 10,
    "totalPages": 1
  },
  "timestamp": 1702018800000
}
```

#### 测试3: 查询工单详情

```bash
curl http://localhost:8080/api/workorders/1
```

#### 测试4: 创建工单

```bash
curl -X POST http://localhost:8080/api/workorders \
  -H "Content-Type: application/json" \
  -d '{
    "workOrderNo": "WO20231208999",
    "productId": 1,
    "productCode": "P001",
    "productName": "Test Product",
    "lineId": "LINE001",
    "planQty": 100,
    "planStartTime": "2023-12-09 08:00:00",
    "planEndTime": "2023-12-09 18:00:00",
    "priority": 3
  }'
```

### 3. 使用浏览器访问

- API基础地址: http://localhost:8080/api
- 工单列表: http://localhost:8080/api/workorders

## 常见问题排查

### 问题1: 端口8080被占用

**错误信息**:
```
Web server failed to start. Port 8080 was already in use.
```

**解决方案**:

方式1: 修改配置文件端口

```yaml
# application.yml
server:
  port: 8081  # 改为其他端口
```

方式2: 停止占用端口的进程

```bash
# 查找占用端口的进程
lsof -i :8080

# 杀死进程
kill -9 <PID>
```

### 问题2: 数据库连接失败

**错误信息**:
```
Communications link failure
```

**解决方案**:

1. 检查MySQL服务是否启动
   ```bash
   mysql.server status
   ```

2. 检查数据库配置
   - 用户名密码是否正确
   - 数据库名称是否正确
   - 主机地址是否正确

3. 测试数据库连接
   ```bash
   mysql -h localhost -u root -p smartmes_lite
   ```

### 问题3: Maven依赖下载失败

**解决方案**:

配置Maven阿里云镜像，编辑 `~/.m2/settings.xml`:

```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

### 问题4: JPA实体映射问题

**错误信息**:
```
Table 'smartmes_lite.work_order' doesn't exist
```

**解决方案**:

1. 确认数据库初始化脚本已执行
   ```bash
   mysql -u root -p smartmes_lite -e "SHOW TABLES;"
   ```

2. 检查JPA配置
   ```yaml
   spring:
     jpa:
       hibernate:
         ddl-auto: update  # 或 validate
   ```

### 问题5: Lombok注解不生效

**解决方案**:

IntelliJ IDEA:
1. File → Settings → Plugins
2. 搜索并安装 "Lombok"
3. File → Settings → Build → Compiler → Annotation Processors
4. 勾选 "Enable annotation processing"

Eclipse:
1. 下载 lombok.jar
2. 双击安装到Eclipse

## 项目结构说明

```
smartmes-backend/
├── src/main/java/com/smartmes/
│   ├── SmartMesApplication.java      # 主启动类
│   ├── common/                       # 通用类
│   │   ├── Result.java              # 统一响应格式
│   │   └── PageResult.java          # 分页结果
│   ├── entity/                       # 实体类
│   │   └── WorkOrder.java           # 工单实体
│   ├── repository/                   # 数据访问层（JPA）
│   │   └── WorkOrderRepository.java
│   ├── service/                      # 业务逻辑层
│   │   ├── WorkOrderService.java
│   │   └── impl/
│   │       └── WorkOrderServiceImpl.java
│   └── controller/                   # 控制器层
│       └── WorkOrderController.java # 工单API
├── src/main/resources/
│   └── application.yml              # 配置文件
├── sql/
│   └── init.sql                     # 数据库初始化脚本
├── pom.xml                          # Maven配置
└── README.md                        # 项目说明
```

## API测试建议

### 推荐工具

1. **Postman** - 图形化API测试工具
2. **curl** - 命令行工具
3. **REST Client** (VS Code插件)

### API端点清单

| 方法 | 端点 | 说明 |
|------|------|------|
| POST | /api/workorders | 创建工单 |
| GET | /api/workorders | 查询工单列表 |
| GET | /api/workorders/{id} | 查询工单详情 |
| GET | /api/workorders/no/{workOrderNo} | 根据工单号查询 |
| PUT | /api/workorders/{id} | 更新工单 |
| DELETE | /api/workorders/{id} | 删除工单 |
| PUT | /api/workorders/{id}/start | 开始工单 |
| PUT | /api/workorders/{id}/complete | 完成工单 |
| PUT | /api/workorders/{id}/cancel | 取消工单 |
| PUT | /api/workorders/{id}/abnormal | 标记异常 |
| PUT | /api/workorders/{id}/progress | 更新进度 |
| GET | /api/workorders/search | 条件查询 |
| GET | /api/workorders/count/{status} | 统计数量 |
| GET | /api/workorders/line/{lineId}/in-progress | 产线进行中工单 |
| GET | /api/workorders/overdue | 逾期工单 |

详细API文档请参考 [README.md](README.md)

## 下一步开发建议

1. **添加认证授权** - 集成Spring Security或JWT
2. **添加API文档** - 集成Swagger/OpenAPI
3. **添加日志监控** - 集成ELK Stack
4. **添加缓存** - 集成Redis
5. **添加消息队列** - 集成RabbitMQ/Kafka
6. **编写单元测试** - JUnit 5 + Mockito
7. **添加前端界面** - React/Vue.js

## 技术支持

如有问题，请检查：
1. 日志文件: `logs/smartmes.log`
2. 控制台输出
3. 数据库连接状态

---

**版本**: 1.0.0
**更新时间**: 2023-12-08
**维护团队**: SmartMES Team
