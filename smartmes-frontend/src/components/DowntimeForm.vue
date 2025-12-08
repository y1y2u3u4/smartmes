<template>
  <div class="downtime-form">
    <el-steps :active="currentStep" finish-status="success" align-center>
      <el-step :title="$t('downtime.selectEquipment')" />
      <el-step :title="$t('downtime.exceptionType')" />
      <el-step :title="$t('downtime.descriptionAndPhoto')" />
    </el-steps>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      class="form-content"
    >
      <!-- Step 1: Select Equipment -->
      <div v-show="currentStep === 0" class="step-container">
        <el-form-item :label="$t('downtime.equipment')" prop="equipmentId">
          <el-select
            v-model="formData.equipmentId"
            :placeholder="$t('downtime.pleaseSelectEquipment')"
            filterable
            size="large"
            style="width: 100%"
          >
            <el-option
              v-for="item in equipmentList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right; color: var(--el-text-color-secondary)">
                {{ item.code }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
      </div>

      <!-- Step 2: Exception Type -->
      <div v-show="currentStep === 1" class="step-container">
        <el-form-item :label="$t('downtime.exceptionType')" prop="exceptionType">
          <el-radio-group v-model="formData.exceptionType" size="large">
            <el-radio-button label="equipment_failure">{{ $t('downtime.exceptionTypes.equipmentFailure') }}</el-radio-button>
            <el-radio-button label="material_shortage">{{ $t('downtime.exceptionTypes.materialShortage') }}</el-radio-button>
            <el-radio-button label="quality_issue">{{ $t('downtime.exceptionTypes.qualityIssue') }}</el-radio-button>
            <el-radio-button label="human_error">{{ $t('downtime.exceptionTypes.humanError') }}</el-radio-button>
            <el-radio-button label="other">{{ $t('downtime.exceptionTypes.other') }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </div>

      <!-- Step 3: Description & Photo -->
      <div v-show="currentStep === 2" class="step-container">
        <el-form-item :label="$t('downtime.description')" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="6"
            :placeholder="$t('downtime.enterDescription')"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item :label="$t('downtime.photoOptional')">
          <el-upload
            ref="uploadRef"
            :file-list="fileList"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :before-upload="beforeUpload"
            :auto-upload="false"
            list-type="picture-card"
            :limit="3"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                {{ $t('downtime.uploadTip') }}
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </div>
    </el-form>

    <!-- Action Buttons -->
    <div class="form-actions">
      <el-button
        v-if="currentStep > 0"
        size="large"
        @click="prevStep"
      >
        {{ $t('common.previous') }}
      </el-button>
      <el-button
        v-if="currentStep < 2"
        type="primary"
        size="large"
        @click="nextStep"
      >
        {{ $t('common.next') }}
      </el-button>
      <el-button
        v-if="currentStep === 2"
        type="primary"
        size="large"
        :loading="submitting"
        @click="handleSubmit"
      >
        {{ $t('downtime.submitReport') }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getEquipmentList, submitDowntimeReport, uploadImage } from '@/api/downtime'

const { t } = useI18n()

// 当前步骤
const currentStep = ref(0)

// 表单引用
const formRef = ref(null)

// 上传组件引用
const uploadRef = ref(null)

// 设备列表
const equipmentList = ref([])

// 文件列表
const fileList = ref([])

// 提交状态
const submitting = ref(false)

// 表单数据
const formData = reactive({
  equipmentId: '',
  exceptionType: '',
  description: '',
  photos: []
})

// 表单验证规则
const rules = computed(() => ({
  equipmentId: [
    { required: true, message: t('downtime.pleaseSelectEquipment'), trigger: 'change' }
  ],
  exceptionType: [
    { required: true, message: t('downtime.pleaseSelectExceptionType'), trigger: 'change' }
  ],
  description: [
    { required: true, message: t('downtime.pleaseEnterDescription'), trigger: 'blur' },
    { min: 10, max: 500, message: t('downtime.descriptionLength'), trigger: 'blur' }
  ]
}))

// 加载设备列表
const loadEquipmentList = async () => {
  try {
    equipmentList.value = await getEquipmentList()
  } catch (error) {
    console.error('Failed to load equipment list:', error)
    ElMessage.error(t('downtime.failedToLoadEquipment'))
  }
}

// 文件变化处理
const handleFileChange = (file, uploadFileList) => {
  fileList.value = uploadFileList
}

// 文件移除处理
const handleFileRemove = (file, uploadFileList) => {
  fileList.value = uploadFileList
}

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error(t('downtime.onlyImageAllowed'))
    return false
  }
  if (!isLt5M) {
    ElMessage.error(t('downtime.imageSizeLimit'))
    return false
  }
  return true
}

// 下一步
const nextStep = async () => {
  // 验证当前步骤的字段
  let fieldsToValidate = []
  if (currentStep.value === 0) {
    fieldsToValidate = ['equipmentId']
  } else if (currentStep.value === 1) {
    fieldsToValidate = ['exceptionType']
  }

  try {
    await formRef.value.validateField(fieldsToValidate)
    currentStep.value++
  } catch (error) {
    console.log('Validation failed:', error)
  }
}

// 上一步
const prevStep = () => {
  currentStep.value--
}

// 上传图片
const uploadPhotos = async () => {
  const photos = []

  for (const file of fileList.value) {
    if (file.raw) {
      const formDataObj = new FormData()
      formDataObj.append('file', file.raw)

      try {
        const response = await uploadImage(formDataObj)
        photos.push(response.url)
      } catch (error) {
        console.error('Failed to upload photo:', error)
        throw error
      }
    }
  }

  return photos
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    submitting.value = true

    // 上传图片
    if (fileList.value.length > 0) {
      formData.photos = await uploadPhotos()
    }

    // 提交表单
    await submitDowntimeReport(formData)

    ElMessage.success(t('downtime.reportSubmitted'))

    // 重置表单
    resetForm()
  } catch (error) {
    console.error('Failed to submit report:', error)
    if (error.errors) {
      // 验证失败
      return
    }
    ElMessage.error(t('downtime.failedToSubmitReport'))
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  currentStep.value = 0
  formRef.value.resetFields()
  fileList.value = []
  formData.photos = []
}

// 初始化
onMounted(() => {
  loadEquipmentList()
})

// 暴露方法给父组件
defineExpose({
  resetForm
})
</script>

<style scoped>
.downtime-form {
  padding: 20px;
}

.form-content {
  margin-top: 40px;
  min-height: 300px;
}

.step-container {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color);
}

/* Radio button group styling */
:deep(.el-radio-button) {
  margin: 5px;
}

:deep(.el-radio-button__inner) {
  padding: 12px 20px;
}

/* Upload styling */
:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 120px;
  height: 120px;
}
</style>
