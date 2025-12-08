<template>
  <el-card class="echarts-card">
    <template #header>
      <div class="card-header">
        <h3>{{ title }}</h3>
        <el-button
          v-if="showRefresh"
          type="text"
          :icon="Refresh"
          :loading="loading"
          @click="handleRefresh"
        >
          {{ $t('common.refresh') }}
        </el-button>
      </div>
    </template>

    <div
      v-loading="loading"
      :style="{ height: height + 'px' }"
    >
      <div
        ref="chartRef"
        :style="{ width: '100%', height: '100%' }"
      />
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  // 卡片标题
  title: {
    type: String,
    required: true
  },
  // 图表配置
  option: {
    type: Object,
    required: true
  },
  // 图表高度
  height: {
    type: Number,
    default: 300
  },
  // 是否显示刷新按钮
  showRefresh: {
    type: Boolean,
    default: false
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['refresh'])

const chartRef = ref(null)
let chartInstance = null

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  // 如果已存在实例，先销毁
  if (chartInstance) {
    chartInstance.dispose()
  }

  // 创建新实例
  chartInstance = echarts.init(chartRef.value)

  // 设置配置
  if (props.option) {
    chartInstance.setOption(props.option)
  }
}

// 更新图表
const updateChart = () => {
  if (chartInstance && props.option) {
    chartInstance.setOption(props.option, true)
  }
}

// 调整图表大小
const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 刷新处理
const handleRefresh = () => {
  emit('refresh')
}

// 监听配置变化
watch(
  () => props.option,
  () => {
    nextTick(() => {
      updateChart()
    })
  },
  { deep: true }
)

// 监听窗口大小变化
let resizeObserver = null

onMounted(() => {
  initChart()

  // 监听窗口大小变化
  window.addEventListener('resize', resizeChart)

  // 使用 ResizeObserver 监听容器大小变化
  if (chartRef.value) {
    resizeObserver = new ResizeObserver(() => {
      resizeChart()
    })
    resizeObserver.observe(chartRef.value)
  }
})

onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('resize', resizeChart)

  // 断开 ResizeObserver
  if (resizeObserver) {
    resizeObserver.disconnect()
  }

  // 销毁图表实例
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

// 暴露方法给父组件
defineExpose({
  resizeChart,
  updateChart
})
</script>

<style scoped>
.echarts-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>
