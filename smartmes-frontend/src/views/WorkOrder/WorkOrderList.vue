<template>
  <div class="work-order-list">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <h2 class="page-title">{{ $t('workOrder.management') }}</h2>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        {{ $t('workOrder.create') }}
      </el-button>
    </div>

    <!-- 搜索过滤区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item :label="$t('workOrder.workOrderNo')">
          <el-input
            v-model="filterForm.workOrderNumber"
            :placeholder="$t('workOrder.enterWorkOrderNumber')"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>

        <el-form-item :label="$t('workOrder.batchNumber')">
          <el-input
            v-model="filterForm.batchNumber"
            :placeholder="$t('workOrder.enterBatchNumber')"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>

        <el-form-item :label="$t('common.status')">
          <el-select
            v-model="filterForm.status"
            :placeholder="$t('workOrder.selectStatus')"
            clearable
            @clear="handleSearch"
          >
            <el-option :label="$t('workOrder.status.pending')" value="pending" />
            <el-option :label="$t('workOrder.status.inProgress')" value="in_progress" />
            <el-option :label="$t('workOrder.status.completed')" value="completed" />
            <el-option :label="$t('workOrder.status.exception')" value="exception" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            {{ $t('common.search') }}
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            {{ $t('common.reset') }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工单表格 -->
    <el-card class="table-card" shadow="never">
      <WorkOrderTable
        :data="tableData"
        :loading="loading"
        @view="handleView"
        @edit="handleEdit"
        @delete="handleDelete"
      />

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshLeft } from '@element-plus/icons-vue'
import WorkOrderTable from '@/components/WorkOrderTable.vue'
import { getWorkOrderList, deleteWorkOrder } from '@/api/workorder'

const router = useRouter()
const { t } = useI18n()

// 过滤表单
const filterForm = reactive({
  workOrderNumber: '',
  batchNumber: '',
  status: ''
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 获取工单列表
const fetchWorkOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      page_size: pagination.pageSize,
      work_order_number: filterForm.workOrderNumber,
      batch_number: filterForm.batchNumber,
      status: filterForm.status
    }

    const response = await getWorkOrderList(params)
    tableData.value = response.data || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('Failed to fetch work orders:', error)
    ElMessage.error(t('workOrder.failedToLoad'))
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchWorkOrders()
}

// 重置
const handleReset = () => {
  filterForm.workOrderNumber = ''
  filterForm.batchNumber = ''
  filterForm.status = ''
  handleSearch()
}

// 创建工单
const handleCreate = () => {
  router.push('/workorders/create')
}

// 查看详情
const handleView = (row) => {
  router.push(`/workorders/${row.id}`)
}

// 编辑工单
const handleEdit = (row) => {
  router.push({
    path: '/workorders/create',
    query: { id: row.id, mode: 'edit' }
  })
}

// 删除工单
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      t('workOrder.deleteConfirmMessage', { workOrderNo: row.work_order_number }),
      t('workOrder.deleteConfirmTitle'),
      {
        confirmButtonText: t('common.delete'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )

    await deleteWorkOrder(row.id)
    ElMessage.success(t('workOrder.deletedSuccessfully'))
    fetchWorkOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete work order:', error)
      ElMessage.error(t('workOrder.failedToDelete'))
    }
  }
}

// 页码改变
const handlePageChange = (page) => {
  pagination.page = page
  fetchWorkOrders()
}

// 每页数量改变
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  fetchWorkOrders()
}

// 页面加载时获取数据
onMounted(() => {
  fetchWorkOrders()
})
</script>

<style scoped>
.work-order-list {
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
  margin: 0;
  color: #1E3A8A;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
