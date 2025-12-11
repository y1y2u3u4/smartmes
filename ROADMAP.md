# SmartMES Lite 项目迭代计划

## 当前项目状态

| 模块 | 完成度 | 状态 |
|------|--------|------|
| 工单管理 | 95% | ✅ 基本完成 |
| 异常停机管理 | 90% | ✅ 基本完成 |
| 生产数据看板 | 100% | ✅ 完成 |
| 基础数据管理 | 85% | ⚠️ 缺少用户管理接口 |
| 认证授权 | 5% | ❌ 仅有数据库设计 |
| 测试覆盖 | 0% | ❌ 无自动化测试 |
| 部署配置 | 100% | ✅ 完成 |

---

## 迭代计划概览

```
Phase 1: 安全基础 (Critical)
    ↓
Phase 2: 质量保障 (High)
    ↓
Phase 3: 功能增强 (Medium)
    ↓
Phase 4: 体验优化 (Low)
    ↓
Phase 5: 高级特性 (Future)
```

---

## Phase 1: 安全基础 (Critical)

> 目标：实现生产环境必需的安全功能

### 1.1 用户认证系统

**后端任务：**
- [ ] 集成 Spring Security 框架
- [ ] 实现 JWT Token 认证
  - 登录接口 `POST /api/auth/login`
  - 刷新 Token 接口 `POST /api/auth/refresh`
  - 登出接口 `POST /api/auth/logout`
- [ ] 密码加密存储 (BCrypt)
- [ ] Token 黑名单机制（登出后失效）

**前端任务：**
- [ ] 登录页面 `LoginView.vue`
- [ ] Token 存储和自动刷新
- [ ] 路由守卫（未登录跳转）
- [ ] 请求拦截器添加 Authorization Header

**涉及文件：**
```
smartmes-backend/
├── config/SecurityConfig.java (新建)
├── controller/AuthController.java (新建)
├── service/AuthService.java (新建)
├── dto/LoginRequest.java (新建)
├── dto/LoginResponse.java (新建)
└── util/JwtUtils.java (新建)

smartmes-frontend/
├── views/Auth/LoginView.vue (新建)
├── store/auth.js (新建)
└── router/index.js (修改)
```

### 1.2 权限控制系统

**后端任务：**
- [ ] 实现基于角色的访问控制 (RBAC)
- [ ] 配置 API 端点权限
  - ADMIN: 全部权限
  - ENGINEER: 工单管理、异常处理、数据查看
  - SUPERVISOR: 异常处理、数据查看
  - OPERATOR: 异常上报、工单查看
- [ ] 添加 `@PreAuthorize` 注解

**前端任务：**
- [ ] 菜单权限控制（根据角色显示/隐藏）
- [ ] 按钮权限控制（禁用无权限操作）
- [ ] 权限指令 `v-permission`

### 1.3 全局异常处理

**后端任务：**
- [ ] 创建全局异常处理器 `@ControllerAdvice`
- [ ] 统一异常响应格式
- [ ] 自定义业务异常类
- [ ] 敏感信息过滤（不暴露堆栈）

**涉及文件：**
```
smartmes-backend/
├── exception/GlobalExceptionHandler.java (新建)
├── exception/BusinessException.java (新建)
└── exception/ErrorCode.java (新建)
```

### 1.4 输入验证加强

**后端任务：**
- [ ] 完善 Entity 的 `@Valid` 注解
- [ ] 添加业务规则验证
  - 数量范围检查 (0-999999)
  - 时间逻辑检查 (开始时间 < 结束时间)
  - 状态转换合法性检查
- [ ] SQL 注入防护检查

---

## Phase 2: 质量保障 (High)

> 目标：建立测试体系，确保代码质量

### 2.1 后端单元测试

**任务：**
- [ ] 配置 JUnit 5 + Mockito
- [ ] Service 层单元测试
  - WorkOrderServiceTest
  - DowntimeServiceTest
  - DashboardServiceTest
- [ ] Repository 层测试 (使用 H2 内存数据库)
- [ ] 目标覆盖率：核心业务 > 80%

**涉及文件：**
```
smartmes-backend/src/test/java/com/smartmes/
├── service/WorkOrderServiceTest.java
├── service/DowntimeServiceTest.java
├── repository/WorkOrderRepositoryTest.java
└── controller/WorkOrderControllerTest.java
```

### 2.2 后端集成测试

**任务：**
- [ ] 配置 Spring Boot Test
- [ ] API 端点集成测试
- [ ] 数据库事务测试
- [ ] 使用 Testcontainers 模拟 MySQL

### 2.3 前端测试

**任务：**
- [ ] 配置 Vitest 测试框架
- [ ] 组件单元测试
  - StatCard.spec.js
  - WorkOrderTable.spec.js
- [ ] API 层 Mock 测试
- [ ] 目标覆盖率：组件 > 60%

### 2.4 E2E 测试

**任务：**
- [ ] 配置 Playwright 或 Cypress
- [ ] 核心流程 E2E 测试
  - 登录流程
  - 工单创建流程
  - 异常上报流程

---

## Phase 3: 功能增强 (Medium)

> 目标：补全缺失功能，增强系统能力

### 3.1 文件上传功能

**后端任务：**
- [ ] 文件上传接口 `POST /api/files/upload`
- [ ] 文件存储服务（本地/MinIO/OSS）
- [ ] 文件类型和大小限制
- [ ] 文件访问接口 `GET /api/files/{id}`

**前端任务：**
- [ ] 完善 DowntimeForm 的图片上传功能
- [ ] 图片预览和删除
- [ ] 上传进度显示

**涉及文件：**
```
smartmes-backend/
├── controller/FileController.java (新建)
├── service/FileStorageService.java (新建)
└── config/FileStorageConfig.java (新建)
```

### 3.2 用户管理模块

**后端任务：**
- [ ] 用户 CRUD 接口
  - `GET /api/users` - 用户列表
  - `POST /api/users` - 创建用户
  - `PUT /api/users/{id}` - 更新用户
  - `DELETE /api/users/{id}` - 删除用户
- [ ] 密码重置功能
- [ ] 用户状态管理（启用/禁用）

**前端任务：**
- [ ] 用户管理页面 `UserManagement.vue`
- [ ] 用户表单组件
- [ ] 个人信息页面

### 3.3 批量操作功能

**后端任务：**
- [ ] 批量创建工单 `POST /api/workorders/batch`
- [ ] 批量更新状态 `PUT /api/workorders/batch/status`
- [ ] 批量删除 `DELETE /api/workorders/batch`

**前端任务：**
- [ ] 表格多选功能
- [ ] 批量操作按钮组
- [ ] 操作确认对话框

### 3.4 数据导入导出

**后端任务：**
- [ ] Excel 导出接口（使用 Apache POI）
  - 工单导出 `GET /api/workorders/export`
  - 异常记录导出 `GET /api/downtime/export`
- [ ] Excel 导入接口
  - 工单批量导入 `POST /api/workorders/import`
- [ ] 导入模板下载

**前端任务：**
- [ ] 导出按钮和格式选择
- [ ] 导入对话框和文件选择
- [ ] 导入结果预览和确认

---

## Phase 4: 体验优化 (Low)

> 目标：提升用户体验和系统性能

### 4.1 实时通知系统

**后端任务：**
- [ ] 集成 WebSocket (Spring WebSocket)
- [ ] 消息推送服务
  - 异常上报通知
  - 工单状态变更通知
- [ ] 消息持久化

**前端任务：**
- [ ] WebSocket 连接管理
- [ ] 通知中心组件
- [ ] 桌面通知权限请求

### 4.2 性能优化

**后端任务：**
- [ ] 集成 Redis 缓存
  - 热点数据缓存（Dashboard 统计）
  - 会话缓存
- [ ] 数据库查询优化
  - 慢查询分析
  - 索引优化
- [ ] 分页查询优化

**前端任务：**
- [ ] 组件懒加载优化
- [ ] 图片懒加载
- [ ] 虚拟滚动（大数据表格）

### 4.3 国际化支持

**前端任务：**
- [ ] 集成 vue-i18n
- [ ] 语言包
  - zh-CN（简体中文）
  - en-US（英文）
- [ ] 语言切换组件
- [ ] 日期/数字格式本地化

### 4.4 主题定制

**前端任务：**
- [ ] Element Plus 主题变量配置
- [ ] 深色模式支持
- [ ] 主题切换组件
- [ ] 用户偏好持久化

---

## Phase 5: 高级特性 (Future)

> 目标：实现高级功能，提升系统价值

### 5.1 报表分析模块

**任务：**
- [ ] 生产效率报表
- [ ] 设备利用率报表
- [ ] 异常分析报表
- [ ] 自定义报表生成器
- [ ] PDF 导出功能

### 5.2 设备监控集成

**任务：**
- [ ] OPC UA / MQTT 协议集成
- [ ] 设备数据采集服务
- [ ] 实时数据展示
- [ ] 报警规则配置

### 5.3 移动端适配

**任务：**
- [ ] 响应式布局优化
- [ ] PWA 支持
- [ ] 移动端专用组件
- [ ] 扫码功能（工单/设备二维码）

### 5.4 审计日志完善

**任务：**
- [ ] 操作日志自动记录（AOP）
- [ ] 日志查询界面
- [ ] 日志导出功能
- [ ] 敏感操作二次确认

### 5.5 工作流引擎

**任务：**
- [ ] 集成 Flowable/Camunda
- [ ] 自定义审批流程
- [ ] 工单审批功能
- [ ] 流程可视化设计器

---

## 迭代里程碑

| 阶段 | 关键交付物 | 优先级 |
|------|-----------|--------|
| **Phase 1** | 安全的可部署系统 | 🔴 Critical |
| **Phase 2** | 质量可控的系统 | 🟠 High |
| **Phase 3** | 功能完整的系统 | 🟡 Medium |
| **Phase 4** | 用户体验优秀的系统 | 🟢 Low |
| **Phase 5** | 具有竞争力的产品 | 🔵 Future |

---

## 技术选型建议

### Phase 1 技术栈
- Spring Security 6.x
- JWT (jjwt 0.12.x)
- BCrypt

### Phase 2 技术栈
- JUnit 5 + Mockito
- Testcontainers
- Vitest + Vue Test Utils
- Playwright

### Phase 3 技术栈
- Apache POI (Excel)
- MinIO / 阿里云 OSS (文件存储)

### Phase 4 技术栈
- Spring WebSocket
- Redis 7.x
- vue-i18n

### Phase 5 技术栈
- Flowable 7.x (工作流)
- ECharts (高级报表)
- MQTT.js (设备通信)

---

## 执行建议

1. **Phase 1 必须优先完成** - 没有认证授权的系统不能上生产
2. **每个 Phase 完成后进行代码审查**
3. **持续集成** - 每次提交自动运行测试
4. **文档同步更新** - 接口变更同步更新 API 文档
5. **定期回顾** - 每个迭代结束后回顾计划调整

---

## 快速开始下一步

如果准备开始 Phase 1，建议按以下顺序执行：

```
1. 全局异常处理 (1.3) - 最简单，立即见效
2. 输入验证加强 (1.4) - 配合异常处理
3. 用户认证系统 (1.1) - 核心功能
4. 权限控制系统 (1.2) - 依赖认证系统
```
