# SmartMES Lite - 快速开始

## 5分钟快速部署

### 前置要求
- ✅ Docker 20.10+
- ✅ Docker Compose 2.0+

### 三步启动

```bash
# 1. 进入项目目录
cd test_demo

# 2. 一键启动
./start.sh

# 3. 访问系统
# 前端: http://localhost
# 默认账号: admin / admin123
```

## 常用命令速查

### 启动和停止

```bash
./start.sh              # 启动（默认模式）
./start.sh dev          # 启动（开发模式）
./start.sh prod         # 启动（生产模式）
./start.sh clean        # 清理所有容器和数据
docker-compose down     # 停止服务
```

### 查看状态和日志

```bash
docker-compose ps           # 查看服务状态
docker-compose logs -f      # 查看所有日志
docker-compose logs -f backend    # 查看后端日志
make logs                   # 使用Makefile查看日志
```

### Makefile快捷命令

```bash
make help           # 查看所有命令
make up             # 启动服务
make down           # 停止服务
make logs           # 查看日志
make ps             # 服务状态
make backup         # 备份数据库
make clean          # 清理所有
```

## 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端应用 | http://localhost | 主要访问入口 |
| 后端API | http://localhost:8080/api | REST API |
| API文档 | http://localhost:8080/swagger-ui.html | Swagger UI |
| 健康检查 | http://localhost:8080/actuator/health | 服务状态 |

## 默认账号

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 全部权限 |
| 生产经理 | manager | manager123 | 查看和编辑 |
| 操作员 | operator | operator123 | 查看和更新工单 |

⚠️ **生产环境请务必修改默认密码！**

## 数据库连接

```
Host: localhost
Port: 3306
Database: smartmes
Username: smartmes
Password: smartmes123
```

## 目录结构

```
test_demo/
├── smartmes-backend/          # 后端代码
├── smartmes-frontend/         # 前端代码
├── database/                  # 数据库脚本
├── docker-compose.yml         # Docker配置
├── start.sh                   # 启动脚本
└── README.md                  # 详细文档
```

## 开发模式

### 启动开发环境

```bash
./start.sh dev
```

### 开发特性
- ✨ 前端热重载（端口5173）
- 🐛 后端远程调试（端口5005）
- 📦 源码目录挂载
- 🔄 自动重启

### 调试配置

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Add → Remote JVM Debug
3. Host: localhost, Port: 5005

**VS Code:**
```json
{
  "type": "java",
  "request": "attach",
  "hostName": "localhost",
  "port": 5005
}
```

## 生产模式

```bash
./start.sh prod
```

### 生产特性
- 🚀 资源限制和优化
- 📊 多副本部署（需Docker Swarm）
- 📝 日志轮转
- 🔒 安全加固

## 常见问题

### 问题1：端口被占用

```bash
# 修改.env文件中的端口
BACKEND_PORT=8081
FRONTEND_PORT=8080

# 重启服务
docker-compose down
docker-compose up -d
```

### 问题2：MySQL无法启动

```bash
# 清理数据卷重新启动
docker-compose down -v
./start.sh
```

### 问题3：无法访问前端

```bash
# 检查服务状态
docker-compose ps

# 查看前端日志
docker-compose logs frontend

# 检查后端健康
curl http://localhost:8080/actuator/health
```

### 问题4：性能慢

```bash
# 检查资源使用
docker stats

# 增加Docker内存（Docker Desktop设置）
# 推荐：4GB RAM, 2 CPUs
```

## 备份和恢复

### 备份数据库

```bash
# 使用Makefile（推荐）
make backup

# 手动备份
docker-compose exec -T mysql mysqldump \
  -u smartmes -psmartmes123 smartmes \
  > backup.sql
```

### 恢复数据库

```bash
# 使用Makefile
make restore

# 手动恢复
docker-compose exec -T mysql mysql \
  -u smartmes -psmartmes123 smartmes \
  < backup.sql
```

## 下一步

- 📖 阅读完整文档：[README.md](README.md)
- 🐳 Docker详细指南：[DOCKER.md](DOCKER.md)
- 📚 API文档：http://localhost:8080/swagger-ui.html

## 获取帮助

遇到问题？

1. 查看日志：`docker-compose logs -f`
2. 查看文档：[DOCKER.md](DOCKER.md) 的故障排查章节
3. 提交Issue：GitHub Issues

---

**SmartMES Lite** - 让制造管理更简单
