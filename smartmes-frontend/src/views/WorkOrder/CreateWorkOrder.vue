<template>
  <div class="create-work-order">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">{{ isEdit ? 'Edit Work Order' : 'Create Work Order' }}</h2>
    </div>

    <!-- 工单表单 -->
    <el-card shadow="never">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="140px"
        class="work-order-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Work Order No." prop="work_order_number">
              <el-input
                v-model="formData.work_order_number"
                placeholder="Auto-generated or enter manually"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Product Code" prop="product_code">
              <el-select
                v-model="formData.product_code"
                placeholder="Select product"
                filterable
              >
                <el-option
                  v-for="product in productList"
                  :key="product.code"
                  :label="`${product.code} - ${product.name}`"
                  :value="product.code"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Batch Number" prop="batch_number">
              <el-input
                v-model="formData.batch_number"
                placeholder="Enter batch number"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Planned Quantity" prop="planned_quantity">
              <el-input-number
                v-model="formData.planned_quantity"
                :min="1"
                :max="999999"
                placeholder="Enter planned quantity"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Production Line" prop="production_line">
              <el-select
                v-model="formData.production_line"
                placeholder="Select production line"
              >
                <el-option label="Production Line 1" value="LINE-001" />
                <el-option label="Production Line 2" value="LINE-002" />
                <el-option label="Production Line 3" value="LINE-003" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Equipment" prop="equipment">
              <el-select
                v-model="formData.equipment"
                placeholder="Select equipment"
                filterable
              >
                <el-option
                  v-for="equip in equipmentList"
                  :key="equip.id"
                  :label="`${equip.code} - ${equip.name}`"
                  :value="equip.code"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Operator" prop="operator">
              <el-select
                v-model="formData.operator"
                placeholder="Select operator"
                filterable
              >
                <el-option
                  v-for="op in operatorList"
                  :key="op.id"
                  :label="`${op.employee_id} - ${op.name}`"
                  :value="op.employee_id"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="Scheduled Start" prop="scheduled_start">
              <el-date-picker
                v-model="formData.scheduled_start"
                type="datetime"
                placeholder="Select start time"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="Notes" prop="notes">
              <el-input
                v-model="formData.notes"
                type="textarea"
                :rows="4"
                placeholder="Enter notes or instructions"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 表单操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? 'Update' : 'Create' }}
          </el-button>
          <el-button @click="handleCancel">Cancel</el-button>
          <el-button @click="handleReset">Reset</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  createWorkOrder,
  updateWorkOrder,
  getWorkOrderDetail,
  getProductList,
  getEquipmentList,
  getOperatorList
} from '@/api/workorder'

const router = useRouter()
const route = useRoute()

// 是否编辑模式
const isEdit = computed(() => route.query.mode === 'edit')

// 表单引用
const formRef = ref(null)

// 表单数据
const formData = reactive({
  work_order_number: '',
  product_code: '',
  batch_number: '',
  planned_quantity: null,
  production_line: '',
  equipment: '',
  operator: '',
  scheduled_start: '',
  notes: ''
})

// 表单验证规则
const formRules = {
  work_order_number: [
    { required: true, message: 'Please enter work order number', trigger: 'blur' }
  ],
  product_code: [
    { required: true, message: 'Please select product', trigger: 'change' }
  ],
  batch_number: [
    { required: true, message: 'Please enter batch number', trigger: 'blur' }
  ],
  planned_quantity: [
    { required: true, message: 'Please enter planned quantity', trigger: 'blur' }
  ],
  production_line: [
    { required: true, message: 'Please select production line', trigger: 'change' }
  ],
  equipment: [
    { required: true, message: 'Please select equipment', trigger: 'change' }
  ],
  operator: [
    { required: true, message: 'Please select operator', trigger: 'change' }
  ]
}

// 下拉选项
const productList = ref([])
const equipmentList = ref([])
const operatorList = ref([])

// 提交状态
const submitting = ref(false)

// 获取下拉选项数据
const fetchOptions = async () => {
  try {
    const [products, equipment, operators] = await Promise.all([
      getProductList(),
      getEquipmentList(),
      getOperatorList()
    ])

    productList.value = products.data || []
    equipmentList.value = equipment.data || []
    operatorList.value = operators.data || []
  } catch (error) {
    console.error('Failed to fetch options:', error)
    ElMessage.error('Failed to load options')
  }
}

// 获取工单详情（编辑模式）
const fetchWorkOrderDetail = async (id) => {
  try {
    const response = await getWorkOrderDetail(id)
    Object.assign(formData, response.data)
  } catch (error) {
    console.error('Failed to fetch work order detail:', error)
    ElMessage.error('Failed to load work order details')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (isEdit.value) {
        await updateWorkOrder(route.query.id, formData)
        ElMessage.success('Work order updated successfully')
      } else {
        await createWorkOrder(formData)
        ElMessage.success('Work order created successfully')
      }

      router.push('/workorders')
    } catch (error) {
      console.error('Failed to submit work order:', error)
      ElMessage.error(`Failed to ${isEdit.value ? 'update' : 'create'} work order`)
    } finally {
      submitting.value = false
    }
  })
}

// 取消
const handleCancel = () => {
  router.back()
}

// 重置表单
const handleReset = () => {
  formRef.value?.resetFields()
}

// 页面加载时
onMounted(async () => {
  await fetchOptions()

  if (isEdit.value && route.query.id) {
    await fetchWorkOrderDetail(route.query.id)
  } else {
    // 自动生成工单号
    formData.work_order_number = `WO-${Date.now()}`
  }
})
</script>

<style scoped>
.create-work-order {
  height: 100%;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #1E3A8A;
}

.work-order-form {
  max-width: 1200px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-select),
:deep(.el-input-number) {
  width: 100%;
}
</style>
