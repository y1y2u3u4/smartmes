<template>
  <div class="production-dashboard">
    <!-- Page Header -->
    <div class="dashboard-header">
      <h1>Production Dashboard</h1>
      <div class="header-actions">
        <el-tag type="info">Auto-refresh: 30s</el-tag>
        <el-button
          type="primary"
          :icon="Refresh"
          :loading="refreshing"
          @click="handleManualRefresh"
        >
          Refresh
        </el-button>
      </div>
    </div>

    <!-- Statistics Cards Row -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <StatCard
          label="Total Work Orders Today"
          :value="stats.totalOrders"
          :icon="Document"
          icon-color="#409EFF"
          icon-bg-color="#ecf5ff"
          :trend="stats.ordersTrend"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <StatCard
          label="Completed Work Orders"
          :value="stats.completedOrders"
          :icon="CircleCheck"
          icon-color="#67C23A"
          icon-bg-color="#f0f9ff"
          :trend="stats.completedTrend"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <StatCard
          label="In Progress Work Orders"
          :value="stats.inProgressOrders"
          :icon="Clock"
          icon-color="#E6A23C"
          icon-bg-color="#fdf6ec"
          :trend="stats.inProgressTrend"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <StatCard
          label="Exception Work Orders"
          :value="stats.exceptionOrders"
          :icon="Warning"
          icon-color="#F56C6C"
          icon-bg-color="#fef0f0"
          :trend="stats.exceptionTrend"
        />
      </el-col>
    </el-row>

    <!-- Production Rate & Equipment Status Row -->
    <el-row :gutter="20" class="chart-row">
      <!-- Production Rate Progress -->
      <el-col :xs="24" :md="12">
        <el-card class="production-rate-card">
          <template #header>
            <h3>Today's Production Completion Rate</h3>
          </template>
          <div v-loading="loading" class="production-rate-content">
            <div class="rate-display">
              <div class="rate-circle">
                <el-progress
                  type="circle"
                  :percentage="productionRate.percentage"
                  :width="180"
                  :stroke-width="12"
                  :color="progressColor"
                >
                  <template #default="{ percentage }">
                    <span class="percentage-value">{{ percentage }}%</span>
                  </template>
                </el-progress>
              </div>
              <div class="rate-details">
                <div class="detail-item">
                  <span class="detail-label">Target Output:</span>
                  <span class="detail-value">{{ productionRate.target }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Actual Output:</span>
                  <span class="detail-value actual">{{ productionRate.actual }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Remaining:</span>
                  <span class="detail-value remaining">{{ productionRate.remaining }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Equipment Status Distribution -->
      <el-col :xs="24" :md="12">
        <EChartsCard
          title="Equipment Status Distribution"
          :option="equipmentStatusOption"
          :height="320"
          :loading="loading"
          :show-refresh="true"
          @refresh="loadEquipmentStatus"
        />
      </el-col>
    </el-row>

    <!-- Exception Type & Equipment Failure Row -->
    <el-row :gutter="20" class="chart-row">
      <!-- Exception Type Distribution -->
      <el-col :xs="24" :md="12">
        <EChartsCard
          title="Exception Type Distribution"
          :option="exceptionTypeOption"
          :height="320"
          :loading="loading"
          :show-refresh="true"
          @refresh="loadExceptionType"
        />
      </el-col>

      <!-- Equipment Failure TOP5 -->
      <el-col :xs="24" :md="12">
        <EChartsCard
          title="Equipment Failure TOP 5"
          :option="equipmentFailureOption"
          :height="320"
          :loading="loading"
          :show-refresh="true"
          @refresh="loadEquipmentFailure"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Document,
  CircleCheck,
  Clock,
  Warning
} from '@element-plus/icons-vue'
import StatCard from '@/components/StatCard.vue'
import EChartsCard from '@/components/EChartsCard.vue'
import {
  getDashboardData,
  getTodayOrderStats,
  getTodayProductionRate,
  getEquipmentStatusDistribution,
  getDowntimeTypeDistribution,
  getEquipmentFailureTop5
} from '@/api/dashboard'

// 数据加载状态
const loading = ref(false)
const refreshing = ref(false)

// 自动刷新定时器
let autoRefreshTimer = null

// 统计数据
const stats = reactive({
  totalOrders: 0,
  completedOrders: 0,
  inProgressOrders: 0,
  exceptionOrders: 0,
  ordersTrend: 0,
  completedTrend: 0,
  inProgressTrend: 0,
  exceptionTrend: 0
})

// 产量完成率
const productionRate = reactive({
  percentage: 0,
  target: 0,
  actual: 0,
  remaining: 0
})

// 进度条颜色
const progressColor = computed(() => {
  if (productionRate.percentage >= 100) return '#67C23A'
  if (productionRate.percentage >= 80) return '#409EFF'
  if (productionRate.percentage >= 60) return '#E6A23C'
  return '#F56C6C'
})

// 设备状态分布图表配置
const equipmentStatusOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    right: 20,
    top: 'center'
  },
  color: ['#67C23A', '#409EFF', '#E6A23C', '#F56C6C', '#909399'],
  series: [
    {
      name: 'Equipment Status',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: []
    }
  ]
})

// 异常类型分布图表配置
const exceptionTypeOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    right: 20,
    top: 'center'
  },
  color: ['#1e4d8b', '#2965b0', '#3b7dd5', '#5a95e0', '#7aadeb'],
  series: [
    {
      name: 'Exception Type',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}\n{d}%'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      data: []
    }
  ]
})

// 设备故障TOP5图表配置
const equipmentFailureOption = ref({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'value',
    name: 'Failure Count'
  },
  yAxis: {
    type: 'category',
    data: []
  },
  series: [
    {
      name: 'Failure Count',
      type: 'bar',
      data: [],
      barWidth: '60%',
      itemStyle: {
        color: '#1e4d8b',
        borderRadius: [0, 4, 4, 0]
      },
      label: {
        show: true,
        position: 'right',
        formatter: '{c}',
        color: '#303133'
      }
    }
  ]
})

// 加载工单统计
const loadOrderStats = async () => {
  try {
    const data = await getTodayOrderStats()
    stats.totalOrders = data.total || 0
    stats.completedOrders = data.completed || 0
    stats.inProgressOrders = data.inProgress || 0
    stats.exceptionOrders = data.exception || 0
    stats.ordersTrend = data.trends?.total || 0
    stats.completedTrend = data.trends?.completed || 0
    stats.inProgressTrend = data.trends?.inProgress || 0
    stats.exceptionTrend = data.trends?.exception || 0
  } catch (error) {
    console.error('Failed to load order stats:', error)
  }
}

// 加载产量完成率
const loadProductionRate = async () => {
  try {
    const data = await getTodayProductionRate()
    productionRate.target = data.target || 0
    productionRate.actual = data.actual || 0
    productionRate.remaining = Math.max(0, productionRate.target - productionRate.actual)
    productionRate.percentage = productionRate.target > 0
      ? Math.round((productionRate.actual / productionRate.target) * 100)
      : 0
  } catch (error) {
    console.error('Failed to load production rate:', error)
  }
}

// 加载设备状态分布
const loadEquipmentStatus = async () => {
  try {
    const data = await getEquipmentStatusDistribution()
    equipmentStatusOption.value.series[0].data = data.map(item => ({
      name: item.name,
      value: item.value
    }))
  } catch (error) {
    console.error('Failed to load equipment status:', error)
  }
}

// 加载异常类型分布
const loadExceptionType = async () => {
  try {
    const data = await getDowntimeTypeDistribution()
    exceptionTypeOption.value.series[0].data = data.map(item => ({
      name: item.name,
      value: item.value
    }))
  } catch (error) {
    console.error('Failed to load exception type:', error)
  }
}

// 加载设备故障TOP5
const loadEquipmentFailure = async () => {
  try {
    const data = await getEquipmentFailureTop5()
    equipmentFailureOption.value.yAxis.data = data.map(item => item.name).reverse()
    equipmentFailureOption.value.series[0].data = data.map(item => item.value).reverse()
  } catch (error) {
    console.error('Failed to load equipment failure:', error)
  }
}

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    // 并行加载所有数据
    await Promise.all([
      loadOrderStats(),
      loadProductionRate(),
      loadEquipmentStatus(),
      loadExceptionType(),
      loadEquipmentFailure()
    ])
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
    ElMessage.error('Failed to load dashboard data')
  } finally {
    loading.value = false
  }
}

// 手动刷新
const handleManualRefresh = async () => {
  refreshing.value = true
  try {
    await loadAllData()
    ElMessage.success('Data refreshed successfully')
  } finally {
    refreshing.value = false
  }
}

// 启动自动刷新
const startAutoRefresh = () => {
  // 每30秒自动刷新
  autoRefreshTimer = setInterval(() => {
    loadAllData()
  }, 30000)
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
    autoRefreshTimer = null
  }
}

// 初始化
onMounted(() => {
  loadAllData()
  startAutoRefresh()
})

// 清理
onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.production-dashboard {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.dashboard-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-row {
  margin-bottom: 20px;
}

.chart-row {
  margin-bottom: 20px;
}

/* Production Rate Card */
.production-rate-card {
  height: 100%;
}

.production-rate-card h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.production-rate-content {
  min-height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.rate-display {
  display: flex;
  align-items: center;
  gap: 40px;
}

.rate-circle {
  flex-shrink: 0;
}

.percentage-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
}

.rate-details {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
  min-width: 100px;
}

.detail-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.detail-value.actual {
  color: #409EFF;
}

.detail-value.remaining {
  color: #E6A23C;
}

/* Responsive Design */
@media (max-width: 768px) {
  .production-dashboard {
    padding: 10px;
  }

  .dashboard-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .dashboard-header h1 {
    font-size: 20px;
  }

  .rate-display {
    flex-direction: column;
    gap: 20px;
  }

  .stats-row .el-col,
  .chart-row .el-col {
    margin-bottom: 20px;
  }
}
</style>
