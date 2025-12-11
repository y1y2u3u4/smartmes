# SmartMES Lite - Docker部署指南

本文档详细说明如何使用Docker部署和管理SmartMES Lite系统。

## 目录

- [快速开始](#快速开始)
- [Docker Compose配置说明](#docker-compose配置说明)
- [环境变量配置](#环境变量配置)
- [服务说明](#服务说明)
- [常用命令](#常用命令)
- [开发模式](#开发模式)
- [生产模式](#生产模式)
- [数据持久化](#数据持久化)
- [网络配置](#网络配置)
- [健康检查](#健康检查)
- [日志管理](#日志管理)
- [备份与恢复](#备份与恢复)
- [性能优化](#性能优化)
- [故障排查](#故障排查)

## 快速开始

### 使用启动脚本（推荐）

```bash
# 一键启动（默认模式）
./start.sh

# 开发模式启动
./start.sh dev

# 生产模式启动
./start.sh prod

# 清理所有容器和数据
./start.sh clean
```

### 使用Makefile

```bash
# 查看所有可用命令
make help

# 启动服务
make up

# 开发模式
make dev

# 生产模式
make prod

# 查看日志
make logs

# 停止服务
make down
```

### 手动启动

```bash
# 1. 复制环境配置
cp .env.example .env

# 2. 启动所有服务
docker-compose up -d

# 3. 查看服务状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f
```

## Docker Compose配置说明

项目包含三个Docker Compose配置文件：

### 1. docker-compose.yml（基础配置）

基础配置文件，定义了所有服务的基本配置：

- MySQL数据库服务
- Spring Boot后端服务
- Nginx前端服务
- 网络配置
- 数据卷配置

### 2. docker-compose.dev.yml（开发配置）

开发环境专用配置，提供：

- 源码热重载
- 远程调试端口（5005）
- 开发服务器端口（5173）
- 源码目录挂载
- Maven/npm缓存

使用方式：
```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

### 3. docker-compose.prod.yml（生产配置）

生产环境配置，包含：

- 资源限制
- 副本配置
- 日志轮转
- 性能优化

使用方式：
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## 环境变量配置

### 创建.env文件

```bash
cp .env.example .env
```

### 主要配置项

```bash
# MySQL配置
MYSQL_ROOT_PASSWORD=smartmes123    # MySQL root密码
MYSQL_DATABASE=smartmes            # 数据库名称
MYSQL_USER=smartmes                # 数据库用户
MYSQL_PASSWORD=smartmes123         # 数据库密码

# 端口配置
BACKEND_PORT=8080                  # 后端端口
FRONTEND_PORT=80                   # 前端端口
MYSQL_PORT=3306                    # MySQL端口

# 安全配置（生产环境必须修改）
JWT_SECRET=your-secret-key         # JWT密钥

# 应用配置
SPRING_PROFILES_ACTIVE=prod        # Spring Profile
TZ=Asia/Shanghai                   # 时区
```

## 服务说明

### MySQL服务

**容器名称**: `smartmes-mysql`

**功能**:
- 数据持久化存储
- 自动初始化数据库
- 健康检查

**端口**: 3306

**数据卷**:
- `mysql-data`: 数据持久化
- `./database/schema.sql`: 表结构初始化
- `./database/init_data.sql`: 初始数据

**健康检查**:
```bash
docker-compose exec mysql mysqladmin ping -h localhost -u root -psmartmes123
```

### 后端服务

**容器名称**: `smartmes-backend`

**功能**:
- Spring Boot REST API
- 业务逻辑处理
- JWT身份认证

**端口**: 8080

**依赖**: MySQL服务必须健康

**健康检查**:
```bash
curl http://localhost:8080/actuator/health
```

**API文档**:
- Swagger UI: http://localhost:8080/swagger-ui.html

### 前端服务

**容器名称**: `smartmes-frontend`

**功能**:
- Vue 3 单页应用
- Nginx静态文件托管
- API反向代理

**端口**: 80

**访问地址**: http://localhost

**Nginx配置**:
- 静态文件缓存
- Gzip压缩
- API代理到后端

## 常用命令

### 服务管理

```bash
# 启动所有服务
docker-compose up -d

# 启动特定服务
docker-compose up -d mysql backend

# 停止所有服务
docker-compose stop

# 停止特定服务
docker-compose stop backend

# 重启服务
docker-compose restart backend

# 删除容器（保留数据）
docker-compose down

# 删除容器和数据
docker-compose down -v
```

### 日志查看

```bash
# 查看所有日志
docker-compose logs

# 实时跟踪日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend

# 查看最近100行日志
docker-compose logs --tail=100 backend

# 查看特定时间的日志
docker-compose logs --since 2024-01-01T00:00:00
```

### 容器操作

```bash
# 查看服务状态
docker-compose ps

# 进入容器shell
docker-compose exec backend bash
docker-compose exec mysql bash
docker-compose exec frontend sh

# 执行单次命令
docker-compose exec backend ls -la

# 查看资源使用
docker stats smartmes-backend smartmes-frontend smartmes-mysql
```

### 镜像管理

```bash
# 构建镜像
docker-compose build

# 重新构建（不使用缓存）
docker-compose build --no-cache

# 构建特定服务
docker-compose build backend

# 拉取最新镜像
docker-compose pull
```

## 开发模式

### 启动开发环境

```bash
# 使用启动脚本
./start.sh dev

# 或使用docker-compose
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

### 开发模式特性

1. **热重载**
   - 前端：修改源码后自动刷新
   - 后端：使用Spring DevTools自动重启

2. **远程调试**
   - 调试端口：5005
   - 配置IDE连接到 `localhost:5005`

3. **源码挂载**
   - 前端：`./smartmes-frontend/src` → `/app/src`
   - 后端：`./smartmes-backend/src` → `/app/src`

4. **开发服务器**
   - Vite开发服务器：http://localhost:5173

### IntelliJ IDEA调试配置

1. 创建Remote JVM Debug配置
2. Host: `localhost`
3. Port: `5005`
4. 启动调试连接

### VS Code调试配置

在`.vscode/launch.json`中添加：

```json
{
  "type": "java",
  "request": "attach",
  "name": "Attach to Backend",
  "hostName": "localhost",
  "port": 5005
}
```

## 生产模式

### 启动生产环境

```bash
# 使用启动脚本
./start.sh prod

# 或使用docker-compose
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 生产模式特性

1. **资源限制**
   - 后端：2 CPU, 2GB RAM
   - 前端：1 CPU, 512MB RAM
   - MySQL：2 CPU, 2GB RAM

2. **多副本**
   - 后端：2个副本（需要Docker Swarm）
   - 前端：2个副本（需要Docker Swarm）

3. **日志管理**
   - 日志轮转
   - 最大文件大小限制
   - 保留文件数量限制

4. **性能优化**
   - JVM参数优化
   - 数据库连接池优化
   - Nginx缓存配置

### 使用Docker Swarm部署多副本

```bash
# 初始化Swarm
docker swarm init

# 部署Stack
docker stack deploy -c docker-compose.yml -c docker-compose.prod.yml smartmes

# 查看服务
docker stack services smartmes

# 扩展服务
docker service scale smartmes_backend=3
```

## 数据持久化

### 数据卷

系统使用Docker命名卷持久化数据：

```yaml
volumes:
  mysql-data:        # MySQL数据
  backend-logs:      # 后端日志
```

### 查看数据卷

```bash
# 列出所有数据卷
docker volume ls

# 查看数据卷详情
docker volume inspect test_demo_mysql-data

# 查看数据卷大小
docker system df -v
```

### 数据卷备份

```bash
# 备份MySQL数据卷
docker run --rm \
  -v test_demo_mysql-data:/data \
  -v $(pwd)/backups:/backup \
  alpine tar czf /backup/mysql-data-backup.tar.gz /data

# 恢复MySQL数据卷
docker run --rm \
  -v test_demo_mysql-data:/data \
  -v $(pwd)/backups:/backup \
  alpine tar xzf /backup/mysql-data-backup.tar.gz -C /
```

## 网络配置

### 网络架构

所有服务在同一个自定义桥接网络中：

```yaml
networks:
  smartmes-network:
    driver: bridge
```

### 服务间通信

- 使用服务名称作为主机名
- 例如：`backend` → `mysql:3306`
- 内部DNS自动解析

### 端口映射

```bash
# 宿主机端口 → 容器端口
80          → frontend:80
8080        → backend:8080
3306        → mysql:3306
```

### 查看网络

```bash
# 列出网络
docker network ls

# 查看网络详情
docker network inspect test_demo_smartmes-network

# 查看容器网络
docker-compose exec backend ip addr
```

## 健康检查

### MySQL健康检查

```bash
# 检查命令
mysqladmin ping -h localhost -u root -psmartmes123

# 参数
interval: 10s      # 检查间隔
timeout: 5s        # 超时时间
retries: 5         # 重试次数
start_period: 30s  # 启动等待期
```

### 后端健康检查

```bash
# 检查命令
curl -f http://localhost:8080/actuator/health

# 参数
interval: 30s
timeout: 10s
retries: 3
start_period: 60s
```

### 前端健康检查

```bash
# 检查命令
curl -f http://localhost:80

# 参数
interval: 30s
timeout: 10s
retries: 3
start_period: 10s
```

### 手动检查健康状态

```bash
# 使用Makefile
make health

# 或手动检查
docker-compose ps
docker inspect smartmes-backend | grep -A 5 Health
```

## 日志管理

### 日志位置

- **容器日志**: `docker-compose logs`
- **应用日志**: 挂载到 `./logs/` 目录
- **MySQL日志**: 容器内 `/var/log/mysql/`

### 日志查看

```bash
# 实时查看所有日志
docker-compose logs -f

# 查看特定服务
docker-compose logs -f backend

# 查看最近N行
docker-compose logs --tail=100 backend

# 查看特定时间段
docker-compose logs --since 2024-01-01T00:00:00
```

### 日志轮转配置

生产模式下自动配置：

```yaml
logging:
  driver: "json-file"
  options:
    max-size: "50m"    # 最大文件大小
    max-file: "5"      # 保留文件数
```

### 手动清理日志

```bash
# 清理所有容器日志
truncate -s 0 $(docker inspect --format='{{.LogPath}}' smartmes-backend)

# 清理应用日志
rm -rf logs/*
```

## 备份与恢复

### 数据库备份

```bash
# 使用Makefile（推荐）
make backup

# 或手动备份
docker-compose exec -T mysql mysqldump \
  -u smartmes -psmartmes123 smartmes \
  > backups/smartmes_$(date +%Y%m%d_%H%M%S).sql
```

### 数据库恢复

```bash
# 使用Makefile
make restore

# 或手动恢复
docker-compose exec -T mysql mysql \
  -u smartmes -psmartmes123 smartmes \
  < backups/smartmes_20240101_120000.sql
```

### 自动备份脚本

创建 `backup-cron.sh`：

```bash
#!/bin/bash
BACKUP_DIR="/path/to/backups"
KEEP_DAYS=7

# 创建备份
docker-compose exec -T mysql mysqldump \
  -u smartmes -psmartmes123 smartmes \
  > "$BACKUP_DIR/smartmes_$(date +%Y%m%d_%H%M%S).sql"

# 删除旧备份
find "$BACKUP_DIR" -name "smartmes_*.sql" \
  -mtime +$KEEP_DAYS -delete
```

添加到crontab：

```bash
# 每天凌晨2点备份
0 2 * * * /path/to/backup-cron.sh
```

## 性能优化

### MySQL优化

在 `database/my.cnf` 中配置：

```ini
[mysqld]
innodb_buffer_pool_size=256M      # InnoDB缓冲池
innodb_log_file_size=64M          # 日志文件大小
max_connections=200               # 最大连接数
query_cache_size=32M              # 查询缓存
```

### 后端优化

JVM参数优化：

```bash
JAVA_OPTS=-Xms1024m -Xmx2048m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError
```

### Nginx优化

在 `nginx.conf` 中：

```nginx
# 启用Gzip压缩
gzip on;
gzip_comp_level 6;

# 静态资源缓存
location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}

# 连接优化
worker_connections 1024;
keepalive_timeout 65;
```

### Docker优化

```bash
# 限制日志大小
docker-compose.yml:
  logging:
    options:
      max-size: "10m"
      max-file: "3"

# 使用BuildKit加速构建
export DOCKER_BUILDKIT=1
docker-compose build
```

## 故障排查

### 常见问题

#### 1. 容器无法启动

```bash
# 查看容器状态
docker-compose ps

# 查看详细日志
docker-compose logs [service]

# 检查端口占用
lsof -i :8080
```

#### 2. 数据库连接失败

```bash
# 检查MySQL是否运行
docker-compose ps mysql

# 测试连接
docker-compose exec backend ping mysql

# 查看MySQL日志
docker-compose logs mysql
```

#### 3. 前端无法访问后端

```bash
# 检查Nginx配置
docker-compose exec frontend cat /etc/nginx/nginx.conf

# 测试后端连接
docker-compose exec frontend curl http://backend:8080/actuator/health

# 检查网络
docker network inspect test_demo_smartmes-network
```

#### 4. 性能问题

```bash
# 查看资源使用
docker stats

# 检查磁盘空间
docker system df

# 清理未使用资源
docker system prune -a
```

### 调试技巧

1. **进入容器调试**
   ```bash
   docker-compose exec backend bash
   docker-compose exec mysql mysql -u root -p
   ```

2. **查看容器详情**
   ```bash
   docker inspect smartmes-backend
   ```

3. **网络诊断**
   ```bash
   docker-compose exec backend apt-get install -y curl
   docker-compose exec backend curl http://mysql:3306
   ```

4. **重建容器**
   ```bash
   docker-compose up -d --force-recreate backend
   ```

### 紧急恢复

```bash
# 停止所有服务
docker-compose down

# 清理所有容器和网络
docker-compose down --remove-orphans

# 清理数据卷（谨慎！）
docker-compose down -v

# 重新启动
./start.sh
```

## 最佳实践

1. **使用.env文件管理配置**
   - 不要提交.env到版本控制
   - 使用.env.example作为模板

2. **定期备份数据**
   - 设置自动备份计划
   - 测试恢复流程

3. **监控日志大小**
   - 配置日志轮转
   - 定期清理旧日志

4. **资源限制**
   - 生产环境设置资源限制
   - 监控资源使用情况

5. **安全措施**
   - 修改默认密码
   - 使用强JWT密钥
   - 限制端口暴露

6. **版本管理**
   - 使用特定版本的镜像
   - 避免使用latest标签

7. **健康检查**
   - 为所有服务配置健康检查
   - 监控健康状态

8. **网络隔离**
   - 使用自定义网络
   - 限制不必要的端口暴露

## 总结

本文档涵盖了SmartMES Lite的Docker部署和管理的各个方面。如有问题，请参考故障排查章节或联系技术支持。
