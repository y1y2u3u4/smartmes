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
      prop="workOrderNo"
      label="Work Order No."
      min-width="140"
      show-overflow-tooltip
    />

    <el-table-column
      prop="productCode"
      label="Product Code"
      min-width="120"
      show-overflow-tooltip
    />

    <el-table-column
      prop="batchNo"
      label="Batch Number"
      min-width="120"
      show-overflow-tooltip
    />

    <el-table-column
      prop="planQty"
      label="Planned Qty"
      width="110"
      align="right"
    >
      <template #default="{ row }">
        {{ formatNumber(row.planQty) }}
      </template>
    </el-table-column>

    <el-table-column
      prop="actualQty"
      label="Actual Qty"
      width="110"
      align="right"
    >
      <template #default="{ row }">
        {{ formatNumber(row.actualQty) }}
      </template>
    </el-table-column>

    <el-table-column
      prop="lineId"
      label="Production Line"
      min-width="130"
    />

    <el-table-column
      prop="equipmentId"
      label="Equipment"
      min-width="120"
    />

    <el-table-column
      prop="operatorId"
      label="Operator"
      width="100"
    />

    <el-table-column
      prop="status"
      label="Status"
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
      prop="createdAt"
      label="Created At"
      width="160"
      show-overflow-tooltip
    >
      <template #default="{ row }">
        {{ formatDateTime(row.createdAt) }}
      </template>
    </el-table-column>

    <el-table-column
      label="Actions"
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
          View
        </el-button>
        <el-button
          type="primary"
          size="small"
          link
          @click="handleEdit(row)"
        >
          <el-icon><Edit /></el-icon>
          Edit
        </el-button>
        <el-button
          type="danger"
          size="small"
          link
          @click="handleDelete(row)"
        >
          <el-icon><Delete /></el-icon>
          Delete
        </el-button>
      </template>
    </el-table-column>

    <template #empty>
      <el-empty description="No work orders found" />
    </template>
  </el-table>
</template>

<script setup>
import { View, Edit, Delete } from '@element-plus/icons-vue'

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
    PENDING: 'info',
    IN_PROGRESS: 'primary',
    COMPLETED: 'success',
    ABNORMAL: 'danger',
    CANCELLED: 'warning',
    CLOSED: ''
  }
  return statusTypeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusTextMap = {
    PENDING: 'Pending',
    IN_PROGRESS: 'In Progress',
    COMPLETED: 'Completed',
    ABNORMAL: 'Abnormal',
    CANCELLED: 'Cancelled',
    CLOSED: 'Closed'
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
  return new Date(dateTime).toLocaleString('en-US', {
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
