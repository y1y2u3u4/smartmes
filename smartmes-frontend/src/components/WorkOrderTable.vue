<template>
  <el-table
    :data="data"
    :loading="loading"
    stripe
    border
    style="width: 100%"
    class="work-order-table"
  >
    <el-table-column type="index" label="#" width="60" align="center" />

    <el-table-column
      prop="work_order_number"
      :label="$t('workOrder.workOrderNo')"
      min-width="140"
      show-overflow-tooltip
    />

    <el-table-column
      prop="product_code"
      :label="$t('workOrder.productCode')"
      min-width="120"
      show-overflow-tooltip
    />

    <el-table-column
      prop="batch_number"
      :label="$t('workOrder.batchNumber')"
      min-width="120"
      show-overflow-tooltip
    />

    <el-table-column
      prop="planned_quantity"
      :label="$t('workOrder.plannedQty')"
      width="110"
      align="right"
    >
      <template #default="{ row }">
        {{ formatNumber(row.planned_quantity) }}
      </template>
    </el-table-column>

    <el-table-column
      prop="actual_quantity"
      :label="$t('workOrder.actualQty')"
      width="110"
      align="right"
    >
      <template #default="{ row }">
        {{ formatNumber(row.actual_quantity) }}
      </template>
    </el-table-column>

    <el-table-column
      prop="production_line"
      :label="$t('workOrder.productionLine')"
      min-width="130"
    />

    <el-table-column
      prop="equipment"
      :label="$t('menu.equipment')"
      min-width="120"
    />

    <el-table-column
      prop="operator"
      :label="$t('workOrder.operator')"
      width="100"
    />

    <el-table-column
      prop="status"
      :label="$t('common.status')"
      width="120"
      align="center"
    >
      <template #default="{ row }">
        <el-tag :type="getStatusType(row.status)" effect="dark">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>
    </el-table-column>

    <el-table-column
      prop="created_at"
      :label="$t('workOrder.createdAt')"
      width="160"
      show-overflow-tooltip
    >
      <template #default="{ row }">
        {{ formatDateTime(row.created_at) }}
      </template>
    </el-table-column>

    <el-table-column
      :label="$t('common.actions')"
      width="180"
      align="center"
      fixed="right"
    >
      <template #default="{ row }">
        <el-button
          type="primary"
          size="small"
          link
          @click="handleView(row)"
        >
          <el-icon><View /></el-icon>
          {{ $t('common.view') }}
        </el-button>
        <el-button
          type="primary"
          size="small"
          link
          @click="handleEdit(row)"
        >
          <el-icon><Edit /></el-icon>
          {{ $t('common.edit') }}
        </el-button>
        <el-button
          type="danger"
          size="small"
          link
          @click="handleDelete(row)"
        >
          <el-icon><Delete /></el-icon>
          {{ $t('common.delete') }}
        </el-button>
      </template>
    </el-table-column>

    <template #empty>
      <el-empty :description="$t('workOrder.noWorkOrdersFound')" />
    </template>
  </el-table>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
import { View, Edit, Delete } from '@element-plus/icons-vue'

const { t, locale } = useI18n()

// Props
defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['view', 'edit', 'delete'])

// 获取状态类型（颜色）
const getStatusType = (status) => {
  const statusTypeMap = {
    pending: 'info',
    in_progress: 'primary',
    completed: 'success',
    exception: 'danger'
  }
  return statusTypeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusTextMap = {
    pending: t('workOrder.status.pending'),
    in_progress: t('workOrder.status.inProgress'),
    completed: t('workOrder.status.completed'),
    exception: t('workOrder.status.exception')
  }
  return statusTextMap[status] || status
}

// 格式化数字
const formatNumber = (value) => {
  if (value === null || value === undefined) return '-'
  return value.toLocaleString()
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

// 查看
const handleView = (row) => {
  emit('view', row)
}

// 编辑
const handleEdit = (row) => {
  emit('edit', row)
}

// 删除
const handleDelete = (row) => {
  emit('delete', row)
}
</script>

<style scoped>
.work-order-table {
  font-size: 14px;
}

:deep(.el-table__header) {
  font-weight: 600;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

:deep(.el-button + .el-button) {
  margin-left: 8px;
}
</style>
