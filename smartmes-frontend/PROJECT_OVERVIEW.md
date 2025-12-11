# SmartMES Lite Frontend - Project Overview

## 项目简介

SmartMES Lite是一个现代化的制造执行系统(MES)前端应用，采用Vue 3 + Element Plus构建，提供工单管理、生产监控、异常报告等核心功能。

## 核心功能

### 1. 工单管理 (Work Order Management)

#### 工单列表 (`/workorders`)
- **功能特性**:
  - 分页表格展示所有工单
  - 多条件搜索：工单号、批次号、状态
  - 状态标签颜色区分：
    - Pending (待处理) - 灰色
    - In Progress (进行中) - 蓝色
    - Completed (已完成) - 绿色
    - Exception (异常) - 红色
  - 快捷操作：查看、编辑、删除
  - 响应式表格，支持斑马纹样式

- **文件位置**: `src/views/WorkOrder/WorkOrderList.vue`

#### 创建/编辑工单 (`/workorders/create`)
- **表单字段**:
  - 工单号（自动生成或手动输入）
  - 产品编码（下拉选择）
  - 批次号
  - 计划数量
  - 产线选择
  - 设备选择
  - 操作员选择
  - 计划开始时间
  - 备注信息

- **功能特性**:
  - 完整的表单验证
  - 下拉选项动态加载
  - 编辑模式自动填充数据
  - 自动生成工单号

- **文件位置**: `src/views/WorkOrder/CreateWorkOrder.vue`

#### 工单详情 (`/workorders/:id`)
- **信息展示**:
  - 基本信息（工单号、产品、批次等）
  - 数量信息（计划数量、实际数量、完成率）
  - 时间信息（计划时间、实际时间）
  - 进度可视化（进度条）

- **操作功能**:
  - 开始生产（Pending状态可用）
  - 完成生产（In Progress状态可用）
  - 报告异常
  - 编辑工单

- **文件位置**: `src/views/WorkOrder/WorkOrderDetail.vue`

### 2. 布局系统 (Layout System)

#### 顶部导航栏 (Header)
- **组件**: `src/components/Layout/AppHeader.vue`
- **功能**:
  - Logo和系统标题
  - 通知图标（带未读数量徽章）
  - 用户头像和下拉菜单
  - 深蓝色主题（#1E3A8A）

#### 侧边菜单栏 (Sidebar)
- **组件**: `src/components/Layout/AppSidebar.vue`
- **功能**:
  - 可折叠菜单
  - 菜单项：
    - Work Orders（工单管理）
    - Dashboard（仪表板）
    - Production（生产管理）
    - Quality（质量管理）
    - Reports（报表）
    - Settings（设置）
  - 图标 + 文字导航
  - 自动高亮当前路由

#### 面包屑导航
- **功能**: 显示当前页面路径
- **位置**: 主内容区顶部

### 3. API集成

#### API配置 (`src/api/config.js`)
- 基础URL配置
- API路径映射
- 状态码定义

#### Axios封装 (`src/api/request.js`)
- 请求拦截器：添加token等认证信息
- 响应拦截器：统一错误处理
- 自动显示错误提示

#### 工单API (`src/api/workorder.js`)
- `getWorkOrderList()` - 获取工单列表
- `getWorkOrderDetail(id)` - 获取工单详情
- `createWorkOrder(data)` - 创建工单
- `updateWorkOrder(id, data)` - 更新工单
- `deleteWorkOrder(id)` - 删除工单
- `startWorkOrder(id)` - 开始工单
- `completeWorkOrder(id, data)` - 完成工单
- `getProductList()` - 获取产品列表
- `getEquipmentList()` - 获取设备列表
- `getOperatorList()` - 获取操作员列表

## 技术栈详情

### 核心框架
- **Vue 3.4.0**: 使用Composition API
- **Vue Router 4.2.0**: 前端路由
- **Vite 5.0.0**: 构建工具

### UI组件库
- **Element Plus 2.5.0**: 企业级UI组件库
- **@element-plus/icons-vue 2.3.0**: 图标库

### 工具库
- **Axios 1.6.0**: HTTP客户端
- **ECharts 5.4.0**: 数据可视化（预留）
- **Pinia 2.1.0**: 状态管理（预留）

## 项目结构

```
smartmes-frontend/
├── public/                      # 静态资源
│   └── logo.svg                # Logo图标
│
├── src/
│   ├── api/                    # API层
│   │   ├── config.js          # API配置
│   │   ├── request.js         # Axios实例
│   │   └── workorder.js       # 工单API
│   │
│   ├── assets/                 # 资源文件
│   │   └── styles/            # 样式文件
│   │
│   ├── components/             # 组件
│   │   ├── Layout/            # 布局组件
│   │   │   ├── AppLayout.vue  # 主布局
│   │   │   ├── AppHeader.vue  # 顶部栏
│   │   │   └── AppSidebar.vue # 侧边栏
│   │   └── WorkOrderTable.vue # 工单表格
│   │
│   ├── router/                 # 路由
│   │   └── index.js           # 路由配置
│   │
│   ├── store/                  # 状态管理（预留）
│   │
│   ├── views/                  # 页面
│   │   └── WorkOrder/         # 工单页面
│   │       ├── WorkOrderList.vue     # 列表
│   │       ├── CreateWorkOrder.vue   # 创建
│   │       └── WorkOrderDetail.vue   # 详情
│   │
│   ├── App.vue                 # 根组件
│   └── main.js                 # 入口文件
│
├── .env                        # 环境变量
├── .gitignore                  # Git忽略
├── index.html                  # HTML模板
├── package.json                # 项目配置
├── vite.config.js             # Vite配置
├── README.md                   # 项目说明
├── SETUP.md                    # 安装指南
└── PROJECT_OVERVIEW.md         # 本文档
```

## UI设计规范

### 颜色方案
- **主色调**: `#1E3A8A` (深蓝色)
- **成功色**: Element Plus默认绿色 `#67c23a`
- **警告色**: Element Plus默认橙色 `#e6a23c`
- **危险色**: Element Plus默认红色 `#f56c6c`
- **背景色**: `#f0f2f5`

### 组件样式
- **卡片**: 圆角8px，白色背景
- **按钮**: 圆角4px
- **表格**: 斑马纹样式，鼠标悬停高亮
- **表单**: 标签宽度140px

### 响应式设计
- 支持桌面端和平板端
- 侧边栏可折叠以节省空间
- 表格自适应宽度

## 路由配置

| 路径 | 名称 | 组件 | 功能 |
|------|------|------|------|
| `/` | - | - | 重定向到 `/workorders` |
| `/workorders` | WorkOrderList | WorkOrderList.vue | 工单列表 |
| `/workorders/create` | CreateWorkOrder | CreateWorkOrder.vue | 创建工单 |
| `/workorders/:id` | WorkOrderDetail | WorkOrderDetail.vue | 工单详情 |

## 状态管理

目前项目使用组件本地状态（`ref`, `reactive`），Pinia已配置但未使用。

**建议使用Pinia的场景**:
- 用户认证状态
- 全局配置信息
- 跨页面共享的数据

## 开发规范

### 代码风格
- 使用Vue 3 Composition API
- 使用`<script setup>`语法
- 使用单文件组件（SFC）
- 组件名使用PascalCase
- 变量名使用camelCase

### 文件命名
- 组件文件：PascalCase（例如：`WorkOrderList.vue`）
- JS文件：camelCase（例如：`workorder.js`）
- 样式文件：kebab-case（例如：`global-styles.css`）

### 注释规范
- API函数使用JSDoc注释
- 复杂逻辑添加中文注释
- 组件添加功能说明注释

## API接口规范

### 请求格式
```javascript
// GET请求
{
  params: {
    page: 1,
    page_size: 10,
    search: 'keyword'
  }
}

// POST/PUT请求
{
  data: {
    field1: 'value1',
    field2: 'value2'
  }
}
```

### 响应格式
```javascript
// 成功响应
{
  code: 200,
  message: 'Success',
  data: { ... },
  total: 100  // 列表接口才有
}

// 错误响应
{
  code: 400,
  message: 'Error message',
  data: null
}
```

## 部署说明

### 开发环境
```bash
npm run dev
```
访问: `http://localhost:5173`

### 生产环境
```bash
# 构建
npm run build

# 预览
npm run preview
```

### 环境变量
- `VITE_API_BASE_URL`: API基础URL（默认：`/api`）

## 扩展开发

### 添加新页面
1. 在`src/views/`创建页面组件
2. 在`src/router/index.js`添加路由
3. 在`src/components/Layout/AppSidebar.vue`添加菜单项

### 添加新API
1. 在`src/api/config.js`定义API路径
2. 在对应的API文件中实现接口函数
3. 在页面组件中调用

### 添加新组件
1. 在`src/components/`创建组件
2. 在需要的页面中导入使用

## 已知问题和待优化

### 待实现功能
- [ ] 用户登录/认证
- [ ] Dashboard仪表板
- [ ] 生产管理模块
- [ ] 质量管理模块
- [ ] 报表功能
- [ ] 数据导出
- [ ] 国际化(i18n)

### 优化建议
- [ ] 添加单元测试
- [ ] 添加E2E测试
- [ ] 添加代码检查工具(ESLint)
- [ ] 添加代码格式化工具(Prettier)
- [ ] 优化打包体积
- [ ] 添加PWA支持

## 性能优化

### 已实现
- 路由懒加载
- Element Plus按需引入（可选）
- Vite构建优化

### 建议优化
- 图片懒加载
- 虚拟滚动（大数据量表格）
- 缓存策略
- CDN加速

## 浏览器兼容性

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 技术支持

如有问题，请联系开发团队或查阅：
- Vue 3文档：https://vuejs.org/
- Element Plus文档：https://element-plus.org/
- Vite文档：https://vitejs.dev/

---

**版本**: 1.0.0
**最后更新**: 2024-12-08
**作者**: SmartMES Lite开发团队
