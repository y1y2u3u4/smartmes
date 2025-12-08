<template>
  <div class="downtime-report-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <h2>{{ $t('downtime.exceptionReport') }}</h2>
          <el-button type="primary" @click="goToList">
            {{ $t('downtime.viewExceptionList') }}
          </el-button>
        </div>
      </template>

      <DowntimeForm ref="formRef" />
    </el-card>

    <!-- Help Tips -->
    <el-card class="tips-card" shadow="never">
      <template #header>
        <h3>{{ $t('downtime.reportingInstructions') }}</h3>
      </template>
      <el-alert
        :title="$t('downtime.quickReportingTips')"
        type="info"
        :closable="false"
        show-icon
      >
        <ul class="tips-list">
          <li>{{ $t('downtime.tip1') }}</li>
          <li>{{ $t('downtime.tip2') }}</li>
          <li>{{ $t('downtime.tip3') }}</li>
          <li>{{ $t('downtime.tip4') }}</li>
          <li>{{ $t('downtime.tip5') }}</li>
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
