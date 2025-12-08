<template>
  <div class="work-order-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <div>
        <h2 class="page-title">Work Order Detail</h2>
        <p class="work-order-number">{{ workOrder.work_order_number }}</p>
      </div>
      <div class="header-actions">
        <el-button @click="handleBack">
          <el-icon><Back /></el-icon>
          Back
        </el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          Edit
        </el-button>
      </div>
    </div>

    <el-card v-loading="loading" shadow="never">
      <!-- 基本信息 -->
      <div class="section">
        <h3 class="section-title">Basic Information</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Work Order No.">
            {{ workOrder.work_order_number }}
          </el-descriptions-item>
          <el-descriptions-item label="Status">
            <el-tag :type="getStatusType(workOrder.status)">
              {{ getStatusText(workOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Product Code">
            {{ workOrder.product_code }}
          </el-descriptions-item>
          <el-descriptions-item label="Batch Number">
            {{ workOrder.batch_number }}
          </el-descriptions-item>
          <el-descriptions-item label="Production Line">
            {{ workOrder.production_line }}
          </el-descriptions-item>
          <el-descriptions-item label="Equipment">
            {{ workOrder.equipment }}
          </el-descriptions-item>
          <el-descriptions-item label="Operator">
            {{ workOrder.operator }}
          </el-descriptions-item>
          <el-descriptions-item label="Scheduled Start">
            {{ formatDateTime(workOrder.scheduled_start) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 数量信息 -->
      <div class="section">
        <h3 class="section-title">Quantity Information</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Planned Quantity">
            {{ workOrder.planned_quantity }}
          </el-descriptions-item>
          <el-descriptions-item label="Actual Quantity">
            {{ workOrder.actual_quantity || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="Completion Rate">
            <el-progress
              v-if="workOrder.actual_quantity"
              :percentage="getCompletionRate()"
              :color="getProgressColor()"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="Defect Quantity">
            {{ workOrder.defect_quantity || 0 }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 时间信息 -->
      <div class="section">
        <h3 class="section-title">Time Information</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Actual Start">
            {{ formatDateTime(workOrder.actual_start) || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="Actual End">
            {{ formatDateTime(workOrder.actual_end) || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="Created At">
            {{ formatDateTime(workOrder.created_at) }}
          </el-descriptions-item>
          <el-descriptions-item label="Updated At">
            {{ formatDateTime(workOrder.updated_at) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 备注 -->
      <div v-if="workOrder.notes" class="section">
        <h3 class="section-title">Notes</h3>
        <p class="notes-content">{{ workOrder.notes }}</p>
      </div>

      <!-- 操作按钮 -->
      <div class="action-section">
        <el-button
          v-if="workOrder.status === 'pending'"
          type="success"
          @click="handleStart"
        >
          Start Production
        </el-button>
        <el-button
          v-if="workOrder.status === 'in_progress'"
          type="primary"
          @click="handleComplete"
        >
          Complete Production
        </el-button>
        <el-button
          v-if="workOrder.status !== 'completed'"
          type="warning"
          @click="handleException"
        >
          Report Exception
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Back, Edit } from '@element-plus/icons-vue'
import { getWorkOrderDetail, startWorkOrder, completeWorkOrder } from '@/api/workorder'

const router = useRouter()
const route = useRoute()

// 工单详情
const workOrder = ref({})
const loading = ref(false)

// 获取工单详情
const fetchWorkOrderDetail = async () => {
  loading.value = true
  try {
    const response = await getWorkOrderDetail(route.params.id)
    workOrder.value = response.data || {}
  } catch (error) {
    console.error('Failed to fetch work order detail:', error)
    ElMessage.error('Failed to load work order details')
  } finally {
    loading.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    pending: 'info',
    in_progress: 'primary',
    completed: 'success',
    exception: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: 'Pending',
    in_progress: 'In Progress',
    completed: 'Completed',
    exception: 'Exception'
  }
  return statusMap[status] || status
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 计算完成率
const getCompletionRate = () => {
  if (!workOrder.value.planned_quantity) return 0
  return Math.round(
    (workOrder.value.actual_quantity / workOrder.value.planned_quantity) * 100
  )
}

// 获取进度条颜色
const getProgressColor = () => {
  const rate = getCompletionRate()
  if (rate < 50) return '#f56c6c'
  if (rate < 80) return '#e6a23c'
  return '#67c23a'
}

// 返回
const handleBack = () => {
  router.back()
}

// 编辑
const handleEdit = () => {
  router.push({
    path: '/workorders/create',
    query: { id: workOrder.value.id, mode: 'edit' }
  })
}

// 开始生产
const handleStart = async () => {
  try {
    await startWorkOrder(workOrder.value.id)
    ElMessage.success('Production started successfully')
    fetchWorkOrderDetail()
  } catch (error) {
    console.error('Failed to start production:', error)
    ElMessage.error('Failed to start production')
  }
}

// 完成生产
const handleComplete = async () => {
  try {
    const { value: actualQuantity } = await ElMessageBox.prompt(
      'Please enter actual quantity produced',
      'Complete Production',
      {
        confirmButtonText: 'Complete',
        cancelButtonText: 'Cancel',
        inputPattern: /^\d+$/,
        inputErrorMessage: 'Please enter a valid number'
      }
    )

    await completeWorkOrder(workOrder.value.id, {
      actual_quantity: parseInt(actualQuantity)
    })
    ElMessage.success('Production completed successfully')
    fetchWorkOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to complete production:', error)
      ElMessage.error('Failed to complete production')
    }
  }
}

// 报告异常
const handleException = () => {
  ElMessage.info('Exception reporting feature coming soon')
}

// 页面加载时
onMounted(() => {
  fetchWorkOrderDetail()
})
</script>

<style scoped>
.work-order-detail {
  height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #1E3A8A;
}

.work-order-number {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.section {
  margin-bottom: 24px;
}

.section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #1E3A8A;
}

.notes-content {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
  white-space: pre-wrap;
  margin: 0;
}

.action-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  gap: 12px;
}
</style>
