# SmartMES Lite Frontend - Project Summary

## 项目概述

SmartMES Lite是一个现代化的制造执行系统(MES)前端应用，专为生产车间异常管理和生产数据可视化而设计。

## 核心功能

### 1. 异常停机上报系统

#### 异常上报页面 (`/downtime/report`)
- **3步上报流程**:
  - 步骤1: 从下拉列表选择设备
  - 步骤2: 单选按钮组选择异常类型
  - 步骤3: 填写描述(10-500字)并可选上传照片

- **异常类型**:
  - 设备故障 (Equipment Failure)
  - 物料缺料 (Material Shortage)
  - 质量异常 (Quality Issue)
  - 人员失误 (Human Error)
  - 其他 (Other)

- **文件上传**:
  - 支持JPG/PNG格式
  - 最多3张照片
  - 单个文件最大5MB

#### 异常列表页面 (`/downtime/list`)
- **表格展示功能**:
  - ID、设备名称、异常类型、描述
  - 状态、停机时长、上报人、上报时间
  - 操作按钮（响应、解决、详情）

- **筛选功能**:
  - 按设备筛选
  - 按异常类型筛选
  - 按状态筛选（待处理/处理中/已解决）

- **状态颜色标识**:
  - 待处理 (Pending) - 红色标签
  - 处理中 (In Progress) - 黄色标签
  - 已解决 (Resolved) - 绿色标签

- **工作流操作**:
  - 响应异常：将状态从"待处理"改为"处理中"
  - 解决异常：将状态从"处理中"改为"已解决"
  - 查看详情：弹窗显示完整异常信息

### 2. 生产数据看板

#### 看板布局 (`/dashboard`)

**第一行 - 统计卡片 (4个)**:
1. 今日工单总数 (Total Work Orders)
   - 图标: Document (蓝色)
   - 趋势指标显示

2. 已完成工单数 (Completed Work Orders)
   - 图标: CircleCheck (绿色)
   - 趋势指标显示

3. 进行中工单数 (In Progress Work Orders)
   - 图标: Clock (黄色)
   - 趋势指标显示

4. 异常工单数 (Exception Work Orders)
   - 图标: Warning (红色)
   - 趋势指标显示

**第二行 - 产量和设备状态**:
1. 今日产量完成率 (左侧)
   - 环形进度条（180px直径）
   - 显示百分比
   - 目标产量、实际产量、剩余产量数值
   - 颜色根据完成率动态变化
     - ≥100%: 绿色
     - ≥80%: 蓝色
     - ≥60%: 黄色
     - <60%: 红色

2. 设备状态分布 (右侧)
   - 饼图（环形）
   - 5种状态：正常、运行、维护、故障、离线
   - 颜色：绿、蓝、黄、红、灰

**第三行 - 异常分析**:
1. 异常类型分布 (左侧)
   - 饼图（环形）
   - 蓝色系渐变配色
   - 数据标签显示名称和百分比

2. 设备故障TOP5 (右侧)
   - 横向柱状图
   - 深蓝色柱子
   - 数据标签在柱子右侧

**自动刷新功能**:
- 每30秒自动刷新所有数据
- 页面顶部显示"Auto-refresh: 30s"标签
- 手动刷新按钮（带Loading状态）

## 技术实现

### 组件架构

#### 页面组件 (Views)
```
views/
├── Dashboard/
│   └── ProductionDashboard.vue    # 生产数据看板主页
└── Downtime/
    ├── DowntimeReport.vue         # 异常上报页面
    └── DowntimeList.vue           # 异常列表页面
```

#### 可复用组件 (Components)
```
components/
├── DowntimeForm.vue    # 异常上报表单（3步向导）
├── StatCard.vue        # 统计卡片（支持趋势指标）
└── EChartsCard.vue     # 图表卡片（带刷新功能）
```

#### API服务层 (API)
```
api/
├── request.js      # Axios配置和拦截器
├── downtime.js     # 异常上报相关API
└── dashboard.js    # 数据看板相关API
```

### UI设计风格

#### 配色方案
- **主色调**: 传统ERP蓝色系
  - Primary Blue: `#409EFF`
  - Deep Blue: `#1e4d8b`
  - Medium Blue: `#2965b0`

- **状态颜色**:
  - Success: `#67C23A` (绿色)
  - Warning: `#E6A23C` (黄色)
  - Danger: `#F56C6C` (红色)
  - Info: `#909399` (灰色)

#### 布局特点
- 顶部导航栏：深蓝渐变背景
- 主内容区：浅灰背景 `#f5f7fa`
- 卡片样式：白色背景，圆角8px，悬停效果
- 响应式设计：支持移动端和桌面端

### ECharts配置

#### 饼图配置
```javascript
- 类型: 环形饼图
- 半径: ['40%', '70%']
- 圆角边框: 10px
- 边框颜色: 白色
- 边框宽度: 2px
- 悬停放大效果
```

#### 柱状图配置
```javascript
- 类型: 横向柱状图
- 柱子宽度: 60%
- 颜色: #1e4d8b
- 圆角: [0, 4, 4, 0]
- 数据标签: 显示在右侧
```

### 响应式设计

#### 断点设置
```css
- 桌面: ≥1200px (4列布局)
- 平板: 768px-1199px (2列布局)
- 移动: <768px (单列布局)
```

#### 移动端优化
- 导航菜单折叠为图标
- 卡片堆叠显示
- 图表自适应容器大小
- 触摸友好的操作按钮

## API接口规范

### 请求格式
```
所有请求统一前缀: /api
认证方式: Bearer Token (存储在localStorage)
```

### 响应格式
```json
{
  "code": 200,
  "message": "Success",
  "data": { ... }
}
```

### 异常上报API

#### 获取设备列表
```
GET /api/equipment/list
Response: Array<{ id, name, code }>
```

#### 提交异常上报
```
POST /api/downtime/report
Body: {
  equipmentId: string,
  exceptionType: string,
  description: string,
  photos: Array<string>
}
```

#### 获取异常列表
```
GET /api/downtime/list?page=1&pageSize=20&equipmentId=&exceptionType=&status=
Response: {
  list: Array<DowntimeRecord>,
  total: number
}
```

#### 响应异常
```
PUT /api/downtime/:id/respond
Body: { response: string }
```

#### 解决异常
```
PUT /api/downtime/:id/resolve
Body: { solution: string }
```

### 数据看板API

#### 获取工单统计
```
GET /api/dashboard/order-stats
Response: {
  total: number,
  completed: number,
  inProgress: number,
  exception: number,
  trends: {
    total: number,
    completed: number,
    inProgress: number,
    exception: number
  }
}
```

#### 获取产量完成率
```
GET /api/dashboard/production-rate
Response: {
  target: number,
  actual: number
}
```

#### 获取设备状态分布
```
GET /api/dashboard/equipment-status
Response: Array<{ name: string, value: number }>
```

#### 获取异常类型分布
```
GET /api/dashboard/downtime-distribution
Response: Array<{ name: string, value: number }>
```

#### 获取设备故障TOP5
```
GET /api/dashboard/equipment-failure-top5
Response: Array<{ name: string, value: number }>
```

## 开发规范

### 代码风格
- Vue 3 Composition API
- 使用 `<script setup>` 语法
- TypeScript类型注释（可选）
- 中文注释，英文UI文本

### 命名规范
- 组件文件: PascalCase (e.g., `StatCard.vue`)
- 组件名称: PascalCase
- API文件: camelCase (e.g., `downtime.js`)
- 变量/函数: camelCase
- 常量: UPPER_SNAKE_CASE

### 提交规范
- feat: 新功能
- fix: 修复问题
- refactor: 重构代码
- style: 样式调整
- docs: 文档更新
- chore: 构建配置

## 性能优化

### 已实现优化
1. **路由懒加载**: 所有页面组件按需加载
2. **图表响应式**: 使用ResizeObserver监听容器变化
3. **API请求优化**: 使用Promise.all并行请求
4. **图片懒加载**: Element Plus上传组件内置
5. **防抖节流**: 图表resize事件防抖

### 建议优化
1. 添加虚拟滚动（长列表）
2. 实现数据缓存策略
3. 添加骨架屏加载
4. 优化图片压缩
5. 启用Gzip压缩

## 部署说明

### 构建命令
```bash
npm run build
```

### 构建产物
```
dist/
├── assets/
│   ├── index-[hash].js
│   ├── index-[hash].css
│   └── [图片资源]
└── index.html
```

### Nginx配置示例
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /path/to/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://backend:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 浏览器兼容性

- Chrome: ≥90
- Firefox: ≥88
- Safari: ≥14
- Edge: ≥90

## 项目统计

- **总文件数**: 27个文件
- **代码行数**: ~3500行
- **组件数量**: 8个Vue组件
- **页面数量**: 3个主要页面
- **API接口**: 13个接口

## 未来扩展

### 计划功能
1. 用户权限管理
2. 数据导出功能
3. 报表生成
4. 实时通知推送
5. 移动端App
6. 多语言支持

### 技术升级
1. TypeScript全面迁移
2. Pinia状态管理
3. 单元测试覆盖
4. E2E测试
5. PWA支持

## 维护指南

### 常见修改

#### 修改API地址
文件: `vite.config.js`
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://your-backend-url',
      changeOrigin: true
    }
  }
}
```

#### 修改刷新间隔
文件: `src/views/Dashboard/ProductionDashboard.vue`
```javascript
// 修改这里的30000（30秒）
autoRefreshTimer = setInterval(() => {
  loadAllData()
}, 30000)
```

#### 修改主题颜色
文件: `src/App.vue`
```css
.app-header {
  background: linear-gradient(135deg, #your-color-1 0%, #your-color-2 100%);
}
```

## 联系方式

- 开发者: SmartMES Team
- 版本: 1.0.0
- 最后更新: 2024

---

**注意**: 本文档持续更新中，如有疑问请联系开发团队。
