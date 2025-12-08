<template>
  <div class="downtime-list-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>{{ $t('downtime.exceptionList') }}</h2>
          <el-button type="primary" @click="goToReport">
            {{ $t('downtime.newReport') }}
          </el-button>
        </div>
      </template>

      <!-- Filter Area -->
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item :label="$t('downtime.equipment')">
          <el-select
            v-model="filterForm.equipmentId"
            :placeholder="$t('downtime.allEquipment')"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in equipmentList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('downtime.exceptionType')">
          <el-select
            v-model="filterForm.exceptionType"
            :placeholder="$t('downtime.allTypes')"
            clearable
            style="width: 200px"
          >
            <el-option :label="$t('downtime.exceptionTypes.equipmentFailure')" value="equipment_failure" />
            <el-option :label="$t('downtime.exceptionTypes.materialShortage')" value="material_shortage" />
            <el-option :label="$t('downtime.exceptionTypes.qualityIssue')" value="quality_issue" />
            <el-option :label="$t('downtime.exceptionTypes.humanError')" value="human_error" />
            <el-option :label="$t('downtime.exceptionTypes.other')" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item :label="$t('common.status')">
          <el-select
            v-model="filterForm.status"
            :placeholder="$t('downtime.allStatus')"
            clearable
            style="width: 150px"
          >
            <el-option :label="$t('downtime.status.pending')" value="pending" />
            <el-option :label="$t('downtime.status.inProgress')" value="in_progress" />
            <el-option :label="$t('downtime.status.resolved')" value="resolved" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">{{ $t('common.search') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>

      <!-- Table -->
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        border
        style="width: 100%"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column
          prop="id"
          label="ID"
          width="80"
          align="center"
        />

        <el-table-column
          prop="equipmentName"
          :label="$t('downtime.equipment')"
          width="150"
        />

        <el-table-column
          prop="exceptionType"
          :label="$t('downtime.exceptionType')"
          width="140"
        >
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.exceptionType)">
              {{ getTypeLabel(row.exceptionType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="description"
          :label="$t('downtime.description')"
          min-width="200"
          show-overflow-tooltip
        />

        <el-table-column
          prop="status"
          :label="$t('common.status')"
          width="110"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="downtimeDuration"
          :label="$t('downtime.downtime')"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <span :class="{ 'long-downtime': row.downtimeDuration > 120 }">
              {{ formatDuration(row.downtimeDuration) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column
          prop="reportedBy"
          :label="$t('downtime.reporter')"
          width="100"
        />

        <el-table-column
          prop="createdAt"
          :label="$t('downtime.reportTime')"
          width="160"
          sortable
        >
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column
          :label="$t('common.actions')"
          width="200"
          align="center"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="warning"
              size="small"
              @click="handleRespond(row)"
            >
              {{ $t('downtime.respond') }}
            </el-button>
            <el-button
              v-if="row.status === 'in_progress'"
              type="success"
              size="small"
              @click="handleResolve(row)"
            >
              {{ $t('downtime.resolve') }}
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handleViewDetail(row)"
            >
              {{ $t('common.details') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- Respond Dialog -->
    <el-dialog
      v-model="respondDialogVisible"
      :title="$t('downtime.respondToException')"
      width="500px"
    >
      <el-form :model="respondForm" label-width="100px">
        <el-form-item :label="$t('downtime.response')">
          <el-input
            v-model="respondForm.response"
            type="textarea"
            :rows="4"
            :placeholder="$t('downtime.enterResponse')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="respondDialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submitRespond">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>

    <!-- Resolve Dialog -->
    <el-dialog
      v-model="resolveDialogVisible"
      :title="$t('downtime.resolveException')"
      width="500px"
    >
      <el-form :model="resolveForm" label-width="100px">
        <el-form-item :label="$t('downtime.solution')">
          <el-input
            v-model="resolveForm.solution"
            type="textarea"
            :rows="4"
            :placeholder="$t('downtime.describeSolution')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveDialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submitResolve">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDowntimeList,
  getEquipmentList,
  respondDowntime,
  resolveDowntime
} from '@/api/downtime'

const router = useRouter()
const { t, locale } = useI18n()

// 数据加载状态
const loading = ref(false)

// 设备列表
const equipmentList = ref([])

// 表格数据
const tableData = ref([])

// 筛选表单
const filterForm = reactive({
  equipmentId: '',
  exceptionType: '',
  status: ''
})

// 分页配置
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

// 响应对话框
const respondDialogVisible = ref(false)
const respondForm = reactive({
  id: '',
  response: ''
})

// 解决对话框
const resolveDialogVisible = ref(false)
const resolveForm = reactive({
  id: '',
  solution: ''
})

// 获取异常类型标签颜色
const getTypeTagType = (type) => {
  const typeMap = {
    equipment_failure: 'danger',
    material_shortage: 'warning',
    quality_issue: 'danger',
    human_error: 'info',
    other: ''
  }
  return typeMap[type] || ''
}

// 获取异常类型标签文本
const getTypeLabel = (type) => {
  const typeMap = {
    equipment_failure: t('downtime.exceptionTypes.equipmentFailure'),
    material_shortage: t('downtime.exceptionTypes.materialShortage'),
    quality_issue: t('downtime.exceptionTypes.qualityIssue'),
    human_error: t('downtime.exceptionTypes.humanError'),
    other: t('downtime.exceptionTypes.other')
  }
  return typeMap[type] || type
}

// 获取状态标签颜色
const getStatusTagType = (status) => {
  const statusMap = {
    pending: 'danger',
    in_progress: 'warning',
    resolved: 'success'
  }
  return statusMap[status] || ''
}

// 获取状态标签文本
const getStatusLabel = (status) => {
  const statusMap = {
    pending: t('downtime.status.pending'),
    in_progress: t('downtime.status.inProgress'),
    resolved: t('downtime.status.resolved')
  }
  return statusMap[status] || status
}

// 格式化时长
const formatDuration = (minutes) => {
  if (!minutes) return '-'
  if (minutes < 60) {
    return `${minutes}m`
  }
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}h ${mins}m`
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const localeStr = locale.value === 'zh-CN' ? 'zh-CN' : 'en-US'
  return date.toLocaleString(localeStr, { hour12: false })
}

// 加载设备列表
const loadEquipmentList = async () => {
  try {
    equipmentList.value = await getEquipmentList()
  } catch (error) {
    console.error('Failed to load equipment list:', error)
  }
}

// 加载异常列表
const loadDowntimeList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...filterForm
    }

    const response = await getDowntimeList(params)
    tableData.value = response.list
    pagination.total = response.total
  } catch (error) {
    console.error('Failed to load downtime list:', error)
    ElMessage.error(t('downtime.failedToLoad'))
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadDowntimeList()
}

// 重置
const handleReset = () => {
  filterForm.equipmentId = ''
  filterForm.exceptionType = ''
  filterForm.status = ''
  handleSearch()
}

// 分页大小变化
const handleSizeChange = () => {
  loadDowntimeList()
}

// 当前页变化
const handleCurrentChange = () => {
  loadDowntimeList()
}

// 响应异常
const handleRespond = (row) => {
  respondForm.id = row.id
  respondForm.response = ''
  respondDialogVisible.value = true
}

// 提交响应
const submitRespond = async () => {
  if (!respondForm.response) {
    ElMessage.warning(t('downtime.pleaseEnterResponse'))
    return
  }

  try {
    await respondDowntime(respondForm.id, {
      response: respondForm.response
    })
    ElMessage.success(t('downtime.responseSubmitted'))
    respondDialogVisible.value = false
    loadDowntimeList()
  } catch (error) {
    console.error('Failed to submit response:', error)
    ElMessage.error(t('downtime.failedToSubmitResponse'))
  }
}

// 解决异常
const handleResolve = (row) => {
  resolveForm.id = row.id
  resolveForm.solution = ''
  resolveDialogVisible.value = true
}

// 提交解决方案
const submitResolve = async () => {
  if (!resolveForm.solution) {
    ElMessage.warning(t('downtime.pleaseDescribeSolution'))
    return
  }

  try {
    await resolveDowntime(resolveForm.id, {
      solution: resolveForm.solution
    })
    ElMessage.success(t('downtime.exceptionResolved'))
    resolveDialogVisible.value = false
    loadDowntimeList()
  } catch (error) {
    console.error('Failed to resolve exception:', error)
    ElMessage.error(t('downtime.failedToResolve'))
  }
}

// 查看详情
const handleViewDetail = (row) => {
  ElMessageBox.alert(
    `
      <div style="line-height: 1.8;">
        <p><strong>${t('downtime.equipment')}:</strong> ${row.equipmentName}</p>
        <p><strong>${t('downtime.exceptionType')}:</strong> ${getTypeLabel(row.exceptionType)}</p>
        <p><strong>${t('downtime.description')}:</strong> ${row.description}</p>
        <p><strong>${t('downtime.reporter')}:</strong> ${row.reportedBy}</p>
        <p><strong>${t('downtime.reportTime')}:</strong> ${formatDateTime(row.createdAt)}</p>
        ${row.respondedAt ? `<p><strong>${t('downtime.responseTime')}:</strong> ${formatDateTime(row.respondedAt)}</p>` : ''}
        ${row.response ? `<p><strong>${t('downtime.response')}:</strong> ${row.response}</p>` : ''}
        ${row.resolvedAt ? `<p><strong>${t('downtime.resolveTime')}:</strong> ${formatDateTime(row.resolvedAt)}</p>` : ''}
        ${row.solution ? `<p><strong>${t('downtime.solution')}:</strong> ${row.solution}</p>` : ''}
      </div>
    `,
    t('downtime.exceptionDetails'),
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: t('common.close')
    }
  )
}

// 跳转到上报页面
const goToReport = () => {
  router.push('/downtime/report')
}

// 初始化
onMounted(() => {
  loadEquipmentList()
  loadDowntimeList()
})
</script>

<style scoped>
.downtime-list-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.filter-form {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.long-downtime {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: 600;
}
</style>
