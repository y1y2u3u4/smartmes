# SmartMES Lite - Quick Start Guide

## 快速启动指南

### 1. 安装依赖

```bash
cd smartmes-frontend
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

应用将在 `http://localhost:5173` 启动

### 3. 访问页面

- **生产数据看板**: http://localhost:5173/dashboard
- **异常上报页面**: http://localhost:5173/downtime/report
- **异常列表页面**: http://localhost:5173/downtime/list

## 功能说明

### 异常上报 (Downtime Report)

**路径**: `/downtime/report`

**功能特性**:
- 3步上报流程
  - 步骤1: 选择设备
  - 步骤2: 选择异常类型（设备故障、物料缺料、质量异常、人员失误、其他）
  - 步骤3: 填写描述并可选上传照片
- 表单验证
- 图片上传（最多3张，每张最大5MB）

### 异常列表 (Downtime List)

**路径**: `/downtime/list`

**功能特性**:
- 表格展示所有异常记录
- 状态颜色标识:
  - 待处理 (Pending) - 红色
  - 处理中 (In Progress) - 黄色
  - 已解决 (Resolved) - 绿色
- 筛选功能:
  - 按设备筛选
  - 按异常类型筛选
  - 按状态筛选
- 操作功能:
  - 响应异常
  - 解决异常
  - 查看详情
- 分页功能

### 生产数据看板 (Production Dashboard)

**路径**: `/dashboard`

**功能特性**:
- 统计卡片（4个）:
  - 今日工单总数
  - 已完成工单数
  - 进行中工单数
  - 异常工单数
  - 趋势指标（上升/下降百分比）

- 图表区域:
  - 产量完成率（环形进度图）
    - 显示目标产量
    - 显示实际产量
    - 显示剩余产量
  - 设备状态分布（饼图）
  - 异常类型分布（饼图）
  - 设备故障TOP5（柱状图）

- 自动刷新:
  - 每30秒自动刷新数据
  - 手动刷新按钮

## 颜色方案

### 主色调
- 主色蓝: `#409EFF`
- 深蓝: `#1e4d8b` 
- 中蓝: `#2965b0`

### 状态颜色
- 成功/正常: `#67C23A` (绿色)
- 警告/处理中: `#E6A23C` (黄色)
- 危险/待处理: `#F56C6C` (红色)
- 信息: `#909399` (灰色)

### 图表配色
- 饼图（设备状态）: 绿、蓝、黄、红、灰
- 饼图（异常类型）: 蓝色系渐变
- 柱状图: 深蓝 `#1e4d8b`

## API配置

### 代理配置

开发环境API代理配置在 `vite.config.js`:

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:3000',
      changeOrigin: true
    }
  }
}
```

### API基础URL

API基础路径: `/api`

请确保后端服务运行在 `http://localhost:3000`

### 接口列表

**异常上报相关**:
- `GET /api/equipment/list` - 获取设备列表
- `POST /api/downtime/report` - 提交异常上报
- `GET /api/downtime/list` - 获取异常列表
- `PUT /api/downtime/:id/respond` - 响应异常
- `PUT /api/downtime/:id/resolve` - 解决异常
- `POST /api/upload/image` - 上传图片

**数据看板相关**:
- `GET /api/dashboard/order-stats` - 获取工单统计
- `GET /api/dashboard/production-rate` - 获取产量完成率
- `GET /api/dashboard/equipment-status` - 获取设备状态分布
- `GET /api/dashboard/downtime-distribution` - 获取异常类型分布
- `GET /api/dashboard/equipment-failure-top5` - 获取设备故障TOP5

## 响应数据格式

所有API响应统一格式:

```json
{
  "code": 200,
  "message": "Success",
  "data": { ... }
}
```

## 项目结构

```
src/
├── api/                    # API服务层
│   ├── request.js         # Axios配置和拦截器
│   ├── downtime.js        # 异常上报API
│   └── dashboard.js       # 数据看板API
├── components/            # 可复用组件
│   ├── DowntimeForm.vue   # 异常上报表单
│   ├── StatCard.vue       # 统计卡片
│   └── EChartsCard.vue    # 图表卡片
├── router/                # 路由配置
│   └── index.js
├── views/                 # 页面组件
│   ├── Dashboard/
│   │   └── ProductionDashboard.vue
│   └── Downtime/
│       ├── DowntimeReport.vue
│       └── DowntimeList.vue
├── App.vue               # 根组件
└── main.js              # 入口文件
```

## 构建部署

### 生产构建

```bash
npm run build
```

构建产物在 `dist/` 目录

### 预览构建

```bash
npm run preview
```

## 注意事项

1. **Node.js版本**: 需要 Node.js >= 16.0.0
2. **后端服务**: 确保后端API服务已启动
3. **图片上传**: 需要配置图片存储服务
4. **浏览器支持**: 推荐使用最新版Chrome、Firefox、Safari或Edge

## 常见问题

### Q: 无法连接后端API
A: 检查 `vite.config.js` 中的代理配置，确保后端服务运行在正确的端口

### Q: 图表不显示
A: 检查浏览器控制台错误，确保ECharts正确加载

### Q: 图片上传失败
A: 检查图片大小（<5MB）和格式（JPG/PNG）

## 技术支持

如有问题，请查看 README.md 或联系开发团队。
