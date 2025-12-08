# SmartMES Lite Frontend - File List

## 项目文件清单

### 配置文件
```
├── package.json              # 项目依赖和脚本配置
├── vite.config.js           # Vite构建配置
├── index.html               # HTML入口文件
├── .gitignore              # Git忽略文件配置
└── .env.example            # 环境变量示例
```

### 文档文件
```
├── README.md               # 项目说明文档
├── QUICKSTART.md          # 快速启动指南
├── PROJECT_SUMMARY.md     # 项目详细总结
└── FILES.md               # 本文件清单
```

### 源代码文件

#### 入口文件
```
src/
├── main.js                # 应用入口文件
└── App.vue                # 根组件
```

#### 路由配置
```
src/router/
└── index.js               # 路由配置文件
```

#### API服务层
```
src/api/
├── request.js             # Axios配置和拦截器
├── downtime.js           # 异常上报API
└── dashboard.js          # 数据看板API
```

#### 页面组件
```
src/views/
├── Dashboard/
│   └── ProductionDashboard.vue    # 生产数据看板页面
└── Downtime/
    ├── DowntimeReport.vue         # 异常上报页面
    └── DowntimeList.vue           # 异常列表页面
```

#### 可复用组件
```
src/components/
├── DowntimeForm.vue       # 异常上报表单组件（3步向导）
├── StatCard.vue          # 统计卡片组件（带趋势）
└── EChartsCard.vue       # 图表卡片组件（带刷新）
```

## 文件详细说明

### 1. 异常上报相关文件

#### DowntimeReport.vue
- **路径**: `src/views/Downtime/DowntimeReport.vue`
- **功能**: 异常上报页面容器
- **包含**: 
  - DowntimeForm组件
  - 帮助提示卡片
  - 页面头部和导航
- **代码行数**: ~80行

#### DowntimeForm.vue
- **路径**: `src/components/DowntimeForm.vue`
- **功能**: 3步向导表单组件
- **步骤**:
  1. 选择设备（下拉选择）
  2. 选择异常类型（单选按钮组）
  3. 填写描述和上传照片
- **验证**: 
  - 设备必选
  - 异常类型必选
  - 描述10-500字
  - 照片最多3张，每张最大5MB
- **代码行数**: ~350行

#### DowntimeList.vue
- **路径**: `src/views/Downtime/DowntimeList.vue`
- **功能**: 异常列表展示和管理
- **特性**:
  - 表格展示
  - 筛选功能（设备/类型/状态）
  - 状态颜色标识
  - 操作按钮（响应/解决/详情）
  - 分页功能
- **代码行数**: ~550行

### 2. 数据看板相关文件

#### ProductionDashboard.vue
- **路径**: `src/views/Dashboard/ProductionDashboard.vue`
- **功能**: 生产数据看板主页
- **布局**:
  - 第一行：4个统计卡片
  - 第二行：产量完成率 + 设备状态分布
  - 第三行：异常类型分布 + 设备故障TOP5
- **特性**:
  - 30秒自动刷新
  - 手动刷新按钮
  - 响应式布局
  - ECharts图表集成
- **代码行数**: ~550行

#### StatCard.vue
- **路径**: `src/components/StatCard.vue`
- **功能**: 可复用的统计卡片组件
- **特性**:
  - 自定义图标和颜色
  - 趋势指标显示
  - 悬停动画效果
  - 单位显示
- **Props**:
  - label: 卡片标签
  - value: 数值
  - unit: 单位
  - icon: 图标组件
  - iconColor: 图标颜色
  - iconBgColor: 图标背景色
  - trend: 趋势值（可选）
- **代码行数**: ~170行

#### EChartsCard.vue
- **路径**: `src/components/EChartsCard.vue`
- **功能**: 可复用的图表卡片组件
- **特性**:
  - ECharts图表封装
  - 响应式自适应
  - 加载状态
  - 刷新功能
  - ResizeObserver监听
- **Props**:
  - title: 卡片标题
  - option: ECharts配置
  - height: 图表高度
  - loading: 加载状态
  - showRefresh: 显示刷新按钮
- **代码行数**: ~160行

### 3. API服务层文件

#### request.js
- **路径**: `src/api/request.js`
- **功能**: Axios实例配置
- **特性**:
  - 请求拦截器（添加Token）
  - 响应拦截器（统一处理错误）
  - 超时设置（10秒）
  - 基础URL配置
- **代码行数**: ~50行

#### downtime.js
- **路径**: `src/api/downtime.js`
- **功能**: 异常上报相关API
- **接口**:
  - getEquipmentList(): 获取设备列表
  - submitDowntimeReport(data): 提交异常上报
  - uploadImage(formData): 上传图片
  - getDowntimeList(params): 获取异常列表
  - respondDowntime(id, data): 响应异常
  - resolveDowntime(id, data): 解决异常
  - getDowntimeDetail(id): 获取异常详情
  - getDowntimeStats(params): 获取异常统计
- **代码行数**: ~90行

#### dashboard.js
- **路径**: `src/api/dashboard.js`
- **功能**: 数据看板相关API
- **接口**:
  - getTodayOrderStats(): 获取今日工单统计
  - getTodayProductionRate(): 获取产量完成率
  - getEquipmentStatusDistribution(): 获取设备状态分布
  - getDowntimeTypeDistribution(): 获取异常类型分布
  - getEquipmentFailureTop5(): 获取设备故障TOP5
  - getDashboardData(): 获取看板全量数据
- **代码行数**: ~60行

### 4. 路由配置文件

#### router/index.js
- **路径**: `src/router/index.js`
- **功能**: Vue Router路由配置
- **路由**:
  - `/` → 重定向到 `/dashboard`
  - `/dashboard` → ProductionDashboard
  - `/downtime/report` → DowntimeReport
  - `/downtime/list` → DowntimeList
- **特性**:
  - 懒加载
  - 路由守卫（设置页面标题）
- **代码行数**: ~50行

### 5. 入口文件

#### main.js
- **路径**: `src/main.js`
- **功能**: 应用入口
- **配置**:
  - Vue应用创建
  - Element Plus注册
  - 全局图标注册
  - 路由挂载
- **代码行数**: ~20行

#### App.vue
- **路径**: `src/App.vue`
- **功能**: 根组件
- **结构**:
  - 顶部导航栏
  - 主内容区（路由视图）
  - 底部版权信息
- **特性**:
  - 响应式导航
  - 页面过渡动画
  - 全局样式定义
- **代码行数**: ~300行

## 代码统计

### 文件类型分布
- Vue组件: 6个文件
- JavaScript: 4个文件
- 配置文件: 4个文件
- 文档文件: 4个文件
- **总计**: 18个文件

### 代码行数统计
- Vue组件: ~2,160行
- JavaScript: ~220行
- 配置文件: ~100行
- **代码总计**: ~2,480行
- **文档总计**: ~1,500行

### 组件复杂度
- 简单组件 (<100行): 2个
  - StatCard.vue
  - DowntimeReport.vue

- 中等复杂度 (100-300行): 2个
  - EChartsCard.vue
  - DowntimeForm.vue

- 复杂组件 (>300行): 2个
  - ProductionDashboard.vue
  - DowntimeList.vue

## 依赖关系

### 组件依赖树
```
App.vue
├── ProductionDashboard.vue
│   ├── StatCard.vue (x4)
│   └── EChartsCard.vue (x3)
├── DowntimeReport.vue
│   └── DowntimeForm.vue
└── DowntimeList.vue
```

### API依赖
```
Components → API Layer → Backend
            ↓
        request.js (Axios配置)
            ↓
    ├── downtime.js
    └── dashboard.js
```

## 文件大小

### 源代码文件
- ProductionDashboard.vue: ~13KB
- DowntimeList.vue: ~13KB
- DowntimeForm.vue: ~8KB
- App.vue: ~6KB
- StatCard.vue: ~3KB
- EChartsCard.vue: ~3KB
- DowntimeReport.vue: ~2KB

### 配置和文档
- README.md: ~8KB
- PROJECT_SUMMARY.md: ~12KB
- QUICKSTART.md: ~5KB
- package.json: ~0.5KB

## 构建产物（预估）

### 开发模式
- 总大小: ~3MB（包含node_modules）
- 主包: ~500KB
- Element Plus: ~1.5MB
- ECharts: ~800KB

### 生产构建
- 压缩后总大小: ~400KB
- HTML: ~2KB
- JS (主包): ~200KB
- CSS: ~100KB
- Vendor (Element+ECharts): ~300KB
- 分块加载: 3-5个chunk

## 维护建议

### 易修改文件
1. **颜色主题**: `src/App.vue` (全局样式部分)
2. **API地址**: `vite.config.js` (proxy配置)
3. **刷新间隔**: `ProductionDashboard.vue` (定时器配置)
4. **路由配置**: `src/router/index.js`

### 扩展点
1. **新增页面**: 在 `src/views/` 添加组件，更新路由
2. **新增API**: 在 `src/api/` 添加API文件
3. **新增组件**: 在 `src/components/` 添加可复用组件
4. **新增样式**: 在 `src/assets/styles/` 添加全局样式

## 测试覆盖（待实现）

### 单元测试
- [ ] DowntimeForm.vue
- [ ] StatCard.vue
- [ ] EChartsCard.vue
- [ ] API服务层

### E2E测试
- [ ] 异常上报流程
- [ ] 异常列表操作
- [ ] 看板数据刷新

---

**最后更新**: 2024-12-08
**维护者**: SmartMES Team
