<template>
  <div class="downtime-report-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <h2>Downtime Exception Report</h2>
          <el-button type="primary" @click="goToList">
            View Exception List
          </el-button>
        </div>
      </template>

      <DowntimeForm ref="formRef" />
    </el-card>

    <!-- Help Tips -->
    <el-card class="tips-card" shadow="never">
      <template #header>
        <h3>Reporting Instructions</h3>
      </template>
      <el-alert
        title="Quick Reporting Tips"
        type="info"
        :closable="false"
        show-icon
      >
        <ul class="tips-list">
          <li>Select the equipment experiencing the exception</li>
          <li>Choose the appropriate exception type</li>
          <li>Provide a detailed description (at least 10 characters)</li>
          <li>Optional: Upload photos to help with troubleshooting</li>
          <li>Submit the report and the maintenance team will be notified immediately</li>
        </ul>
      </el-alert>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import DowntimeForm from '@/components/DowntimeForm.vue'

const router = useRouter()
const formRef = ref(null)

// 跳转到异常列表
const goToList = () => {
  router.push('/downtime/list')
}
</script>

<style scoped>
.downtime-report-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-card {
  margin-bottom: 20px;
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

.tips-card {
  background-color: #f9fafb;
}

.tips-card h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.tips-list {
  margin: 10px 0 0 0;
  padding-left: 20px;
  line-height: 1.8;
}

.tips-list li {
  color: #606266;
  margin-bottom: 8px;
}
</style>
