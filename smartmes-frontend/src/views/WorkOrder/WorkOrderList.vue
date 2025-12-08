<template>
  <div class="work-order-list">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <h2 class="page-title">Work Order Management</h2>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        Create Work Order
      </el-button>
    </div>

    <!-- 搜索过滤区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="Work Order No.">
          <el-input
            v-model="filterForm.workOrderNumber"
            placeholder="Enter work order number"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>

        <el-form-item label="Batch Number">
          <el-input
            v-model="filterForm.batchNumber"
            placeholder="Enter batch number"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>

        <el-form-item label="Status">
          <el-select
            v-model="filterForm.status"
            placeholder="Select status"
            clearable
            @clear="handleSearch"
          >
            <el-option label="Pending" value="pending" />
            <el-option label="In Progress" value="in_progress" />
            <el-option label="Completed" value="completed" />
            <el-option label="Exception" value="exception" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            Search
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            Reset
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshLeft } from '@element-plus/icons-vue'
import WorkOrderTable from '@/components/WorkOrderTable.vue'
import { getWorkOrderList, deleteWorkOrder } from '@/api/workorder'

const router = useRouter()

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
    ElMessage.error('Failed to load work orders')
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
      `Are you sure you want to delete work order "${row.work_order_number}"?`,
      'Delete Confirmation',
      {
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await deleteWorkOrder(row.id)
    ElMessage.success('Work order deleted successfully')
    fetchWorkOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete work order:', error)
      ElMessage.error('Failed to delete work order')
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
