<template>
  <div class="work-order-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <div>
        <h2 class="page-title">{{ $t('workOrder.detail') }}</h2>
        <p class="work-order-number">{{ workOrder.work_order_number }}</p>
      </div>
      <div class="header-actions">
        <el-button @click="handleBack">
          <el-icon><Back /></el-icon>
          {{ $t('common.back') }}
        </el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          {{ $t('common.edit') }}
        </el-button>
      </div>
    </div>

    <el-card v-loading="loading" shadow="never">
      <!-- 基本信息 -->
      <div class="section">
        <h3 class="section-title">{{ $t('workOrder.basicInformation') }}</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="$t('workOrder.workOrderNo')">
            {{ workOrder.work_order_number }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('common.status')">
            <el-tag :type="getStatusType(workOrder.status)">
              {{ getStatusText(workOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.productCode')">
            {{ workOrder.product_code }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.batchNumber')">
            {{ workOrder.batch_number }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.productionLine')">
            {{ workOrder.production_line }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('menu.equipment')">
            {{ workOrder.equipment }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.operator')">
            {{ workOrder.operator }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.scheduledStart')">
            {{ formatDateTime(workOrder.scheduled_start) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 数量信息 -->
      <div class="section">
        <h3 class="section-title">{{ $t('workOrder.quantityInformation') }}</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="$t('workOrder.plannedQty')">
            {{ workOrder.planned_quantity }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.actualQty')">
            {{ workOrder.actual_quantity || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.completionRate')">
            <el-progress
              v-if="workOrder.actual_quantity"
              :percentage="getCompletionRate()"
              :color="getProgressColor()"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.defectQuantity')">
            {{ workOrder.defect_quantity || 0 }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 时间信息 -->
      <div class="section">
        <h3 class="section-title">{{ $t('workOrder.timeInformation') }}</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="$t('workOrder.actualStart')">
            {{ formatDateTime(workOrder.actual_start) || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.actualEnd')">
            {{ formatDateTime(workOrder.actual_end) || '-' }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.createdAt')">
            {{ formatDateTime(workOrder.created_at) }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('workOrder.updatedAt')">
            {{ formatDateTime(workOrder.updated_at) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 备注 -->
      <div v-if="workOrder.notes" class="section">
        <h3 class="section-title">{{ $t('workOrder.notes') }}</h3>
        <p class="notes-content">{{ workOrder.notes }}</p>
      </div>

      <!-- 操作按钮 -->
      <div class="action-section">
        <el-button
          v-if="workOrder.status === 'pending'"
          type="success"
          @click="handleStart"
        >
          {{ $t('workOrder.startProduction') }}
        </el-button>
        <el-button
          v-if="workOrder.status === 'in_progress'"
          type="primary"
          @click="handleComplete"
        >
          {{ $t('workOrder.completeProduction') }}
        </el-button>
        <el-button
          v-if="workOrder.status !== 'completed'"
          type="warning"
          @click="handleException"
        >
          {{ $t('workOrder.reportException') }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Back, Edit } from '@element-plus/icons-vue'
import { getWorkOrderDetail, startWorkOrder, completeWorkOrder } from '@/api/workorder'

const router = useRouter()
const route = useRoute()
const { t, locale } = useI18n()

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
    ElMessage.error(t('workOrder.failedToLoadDetail'))
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
    pending: t('workOrder.status.pending'),
    in_progress: t('workOrder.status.inProgress'),
    completed: t('workOrder.status.completed'),
    exception: t('workOrder.status.exception')
  }
  return statusMap[status] || status
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const localeStr = locale.value === 'zh-CN' ? 'zh-CN' : 'en-US'
  return new Date(dateTime).toLocaleString(localeStr, {
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
    ElMessage.success(t('workOrder.productionStarted'))
    fetchWorkOrderDetail()
  } catch (error) {
    console.error('Failed to start production:', error)
    ElMessage.error(t('workOrder.failedToStart'))
  }
}

// 完成生产
const handleComplete = async () => {
  try {
    const { value: actualQuantity } = await ElMessageBox.prompt(
      t('workOrder.enterActualQuantity'),
      t('workOrder.completeProduction'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        inputPattern: /^\d+$/,
        inputErrorMessage: t('workOrder.enterValidNumber')
      }
    )

    await completeWorkOrder(workOrder.value.id, {
      actual_quantity: parseInt(actualQuantity)
    })
    ElMessage.success(t('workOrder.productionCompleted'))
    fetchWorkOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to complete production:', error)
      ElMessage.error(t('workOrder.failedToComplete'))
    }
  }
}

// 报告异常
const handleException = () => {
  ElMessage.info(t('workOrder.exceptionFeatureComingSoon'))
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
