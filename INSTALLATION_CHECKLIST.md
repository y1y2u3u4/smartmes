# SmartMES Lite - 安装验证清单

本清单用于验证Docker容器化配置是否正确安装和配置。

## 文件验证清单

### ✅ 核心配置文件

- [ ] `docker-compose.yml` - 基础Docker Compose配置
- [ ] `docker-compose.dev.yml` - 开发环境配置
- [ ] `docker-compose.prod.yml` - 生产环境配置
- [ ] `.env.example` - 环境变量模板
- [ ] `.dockerignore` - Docker忽略文件
- [ ] `.gitignore` - Git忽略文件
- [ ] `Makefile` - Make命令集合

### ✅ 启动脚本

- [ ] `start.sh` - 一键启动脚本（可执行）
- [ ] `stop.sh` - 停止服务脚本（可执行）
- [ ] `database/init-db.sh` - 数据库初始化脚本（可执行）

### ✅ 后端Docker配置

- [ ] `smartmes-backend/Dockerfile` - 生产环境Dockerfile
- [ ] `smartmes-backend/Dockerfile.dev` - 开发环境Dockerfile
- [ ] `smartmes-backend/.dockerignore` - 后端忽略文件

### ✅ 前端Docker配置

- [ ] `smartmes-frontend/Dockerfile` - 生产环境Dockerfile
- [ ] `smartmes-frontend/Dockerfile.dev` - 开发环境Dockerfile
- [ ] `smartmes-frontend/nginx.conf` - Nginx配置文件
- [ ] `smartmes-frontend/.dockerignore` - 前端忽略文件

### ✅ 数据库配置

- [ ] `database/my.cnf` - MySQL配置文件
- [ ] `database/schema.sql` - 数据库表结构
- [ ] `database/init_data.sql` - 初始数据

### ✅ 文档文件

- [ ] `README.md` - 主要项目文档
- [ ] `DOCKER.md` - Docker详细指南
- [ ] `QUICK_START.md` - 快速开始指南
- [ ] `DOCKER_FILES_SUMMARY.md` - Docker文件总结
- [ ] `INSTALLATION_CHECKLIST.md` - 本清单

## 功能验证清单

### 步骤1：环境检查

```bash
# 检查Docker版本
docker --version
# 预期输出: Docker version 20.10.x 或更高

# 检查Docker Compose版本
docker-compose --version
# 预期输出: Docker Compose version 2.x.x 或更高

# 检查Docker是否运行
docker info
# 预期输出: 显示Docker信息，无错误
```

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤2：文件权限检查

```bash
# 检查脚本是否可执行
ls -l start.sh stop.sh database/init-db.sh

# 如果不可执行，执行以下命令
chmod +x start.sh stop.sh database/init-db.sh
```

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤3：配置文件检查

```bash
# 复制环境配置
cp .env.example .env

# 验证.env文件存在
ls -la .env

# 查看配置内容（可选）
cat .env
```

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤4：启动测试（默认模式）

```bash
# 使用启动脚本
./start.sh

# 或使用docker-compose
docker-compose up -d
```

**预期输出**:
- ✅ Docker is running
- ✅ Docker Compose is available
- ✅ Services started
- ✅ MySQL is ready
- ✅ Services started successfully

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤5：服务状态检查

```bash
# 查看所有服务状态
docker-compose ps

# 预期输出：所有服务状态为 "Up (healthy)"
```

**预期服务**:
- [ ] smartmes-mysql (Up, healthy)
- [ ] smartmes-backend (Up, healthy)
- [ ] smartmes-frontend (Up, healthy)

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤6：健康检查

```bash
# 检查MySQL
docker-compose exec mysql mysqladmin ping -h localhost -u root -psmartmes123

# 检查后端
curl -f http://localhost:8080/actuator/health

# 检查前端
curl -f http://localhost/
```

**预期输出**:
- MySQL: `mysqld is alive`
- 后端: `{"status":"UP"}`
- 前端: HTML内容

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤7：访问测试

#### 前端访问
- [ ] 浏览器打开 http://localhost
- [ ] 页面正常加载，无错误
- [ ] 可以看到登录界面

#### 后端API访问
- [ ] 浏览器打开 http://localhost:8080/swagger-ui.html
- [ ] Swagger文档正常显示
- [ ] API端点列表完整

#### 登录测试
- [ ] 使用账号 `admin/admin123` 登录
- [ ] 登录成功，跳转到主页
- [ ] 可以查看订单、工单等页面

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤8：日志检查

```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mysql
```

**验证项**:
- [ ] 没有ERROR级别的日志
- [ ] 后端启动成功
- [ ] 前端Nginx运行正常
- [ ] MySQL初始化完成

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤9：数据验证

```bash
# 连接到MySQL
docker-compose exec mysql mysql -u smartmes -psmartmes123 smartmes

# 执行查询（在MySQL命令行中）
SHOW TABLES;
SELECT COUNT(*) FROM users;
SELECT username FROM users;
```

**预期结果**:
- [ ] 至少有10个表
- [ ] users表有至少3条记录
- [ ] 可以看到admin, manager, operator用户

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤10：开发模式测试

```bash
# 停止当前服务
docker-compose down

# 启动开发模式
./start.sh dev

# 或
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

**验证项**:
- [ ] 服务正常启动
- [ ] 远程调试端口5005可访问
- [ ] Vite开发服务器端口5173可访问
- [ ] 源码修改后自动重载

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤11：生产模式测试

```bash
# 停止当前服务
docker-compose down

# 启动生产模式
./start.sh prod

# 或
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

**验证项**:
- [ ] 服务正常启动
- [ ] 资源限制生效
- [ ] 日志轮转配置生效
- [ ] 性能稳定

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤12：备份和恢复测试

```bash
# 创建备份
make backup
# 或
docker-compose exec -T mysql mysqldump -u smartmes -psmartmes123 smartmes > backup_test.sql

# 验证备份文件
ls -lh backup_test.sql

# 恢复测试（可选）
# docker-compose exec -T mysql mysql -u smartmes -psmartmes123 smartmes < backup_test.sql
```

**验证项**:
- [ ] 备份文件创建成功
- [ ] 备份文件大小合理（>10KB）
- [ ] 备份文件包含SQL语句

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤13：Makefile命令测试

```bash
# 测试各个命令
make help      # 显示帮助
make ps        # 查看状态
make logs      # 查看日志（Ctrl+C退出）
make stats     # 查看资源使用
```

**验证项**:
- [ ] 所有命令正常执行
- [ ] 输出信息正确
- [ ] 无错误提示

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 步骤14：停止和清理测试

```bash
# 使用停止脚本
./stop.sh

# 验证服务已停止
docker-compose ps

# 清理测试（可选，会删除数据）
./start.sh clean
```

**验证项**:
- [ ] 所有服务正常停止
- [ ] 容器已删除
- [ ] 清理功能正常工作

**验证结果**: [ ] 通过 / [ ] 未通过

---

## 性能验证

### 资源使用检查

```bash
docker stats smartmes-backend smartmes-frontend smartmes-mysql
```

**标准参考**:
- MySQL: < 500MB 内存
- 后端: < 1GB 内存
- 前端: < 100MB 内存

**实际使用**:
- MySQL: ______ MB
- 后端: ______ MB
- 前端: ______ MB

**验证结果**: [ ] 通过 / [ ] 未通过

---

### 响应时间检查

```bash
# 测试后端API响应时间
time curl http://localhost:8080/actuator/health

# 测试前端加载时间
time curl http://localhost/
```

**标准参考**:
- 后端API: < 1秒
- 前端页面: < 2秒

**实际时间**:
- 后端API: ______ 秒
- 前端页面: ______ 秒

**验证结果**: [ ] 通过 / [ ] 未通过

---

## 安全验证

### 配置安全检查

- [ ] 默认密码已修改（生产环境）
- [ ] JWT密钥已修改（生产环境）
- [ ] 不必要的端口未暴露
- [ ] 容器以非root用户运行
- [ ] .env文件不在版本控制中

**验证结果**: [ ] 通过 / [ ] 未通过

---

## 文档验证

### 文档完整性

- [ ] README.md 包含项目介绍和使用说明
- [ ] DOCKER.md 包含详细的Docker指南
- [ ] QUICK_START.md 包含快速开始步骤
- [ ] 所有文档中的链接有效
- [ ] 代码示例可以正常执行

**验证结果**: [ ] 通过 / [ ] 未通过

---

## 最终验证结果

### 验证总结

| 类别 | 验证项 | 通过 | 未通过 |
|------|--------|------|--------|
| 环境检查 | 1 | [ ] | [ ] |
| 文件权限 | 1 | [ ] | [ ] |
| 配置文件 | 1 | [ ] | [ ] |
| 启动测试 | 1 | [ ] | [ ] |
| 服务状态 | 1 | [ ] | [ ] |
| 健康检查 | 1 | [ ] | [ ] |
| 访问测试 | 1 | [ ] | [ ] |
| 日志检查 | 1 | [ ] | [ ] |
| 数据验证 | 1 | [ ] | [ ] |
| 开发模式 | 1 | [ ] | [ ] |
| 生产模式 | 1 | [ ] | [ ] |
| 备份恢复 | 1 | [ ] | [ ] |
| Makefile | 1 | [ ] | [ ] |
| 停止清理 | 1 | [ ] | [ ] |
| 性能验证 | 2 | [ ] | [ ] |
| 安全验证 | 1 | [ ] | [ ] |
| 文档验证 | 1 | [ ] | [ ] |

**总计**: _____ / 17 项通过

---

## 常见问题处理

### 问题1：Docker未运行
**现象**: `Cannot connect to Docker daemon`

**解决**:
```bash
# macOS/Windows: 启动Docker Desktop
# Linux: 启动Docker服务
sudo systemctl start docker
```

---

### 问题2：端口被占用
**现象**: `port is already allocated`

**解决**:
```bash
# 查找占用端口的进程
lsof -i :8080

# 修改.env文件中的端口
nano .env
```

---

### 问题3：权限不足
**现象**: `Permission denied`

**解决**:
```bash
# 添加执行权限
chmod +x start.sh stop.sh database/init-db.sh

# 或使用bash执行
bash start.sh
```

---

### 问题4：MySQL无法启动
**现象**: MySQL容器反复重启

**解决**:
```bash
# 清理数据卷
docker-compose down -v

# 重新启动
./start.sh
```

---

### 问题5：内存不足
**现象**: 服务启动缓慢或失败

**解决**:
```bash
# Docker Desktop设置中增加内存
# 推荐: 4GB RAM, 2 CPUs

# 或减少服务资源使用
# 编辑docker-compose.prod.yml
```

---

## 验证完成

### 签署确认

- **验证日期**: _______________
- **验证人员**: _______________
- **验证环境**: [ ] 开发 [ ] 测试 [ ] 生产
- **总体评估**: [ ] 通过 [ ] 部分通过 [ ] 未通过

### 备注

```
在此记录验证过程中遇到的问题、解决方案和其他说明：






```

---

**SmartMES Lite** - Docker容器化配置验证完成
