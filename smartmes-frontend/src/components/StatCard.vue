<template>
  <el-card class="stat-card" :body-style="{ padding: '20px' }">
    <div class="stat-content">
      <div class="stat-icon" :style="{ backgroundColor: iconBgColor }">
        <component :is="icon" :style="{ color: iconColor }" />
      </div>
      <div class="stat-info">
        <div class="stat-label">{{ label }}</div>
        <div class="stat-value">
          <span class="value-number">{{ value }}</span>
          <span v-if="unit" class="value-unit">{{ unit }}</span>
        </div>
        <div v-if="trend !== undefined" class="stat-trend">
          <el-icon :class="trendClass">
            <component :is="trendIcon" />
          </el-icon>
          <span :class="trendClass">{{ trendText }}</span>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import {
  ArrowUp,
  ArrowDown,
  Minus
} from '@element-plus/icons-vue'

const props = defineProps({
  // 卡片标签
  label: {
    type: String,
    required: true
  },
  // 数值
  value: {
    type: [Number, String],
    required: true
  },
  // 单位
  unit: {
    type: String,
    default: ''
  },
  // 图标
  icon: {
    type: Object,
    required: true
  },
  // 图标颜色
  iconColor: {
    type: String,
    default: '#409EFF'
  },
  // 图标背景色
  iconBgColor: {
    type: String,
    default: '#ecf5ff'
  },
  // 趋势（正数表示上升，负数表示下降，0表示持平）
  trend: {
    type: Number,
    default: undefined
  }
})

// 趋势图标
const trendIcon = computed(() => {
  if (props.trend === undefined) return null
  if (props.trend > 0) return ArrowUp
  if (props.trend < 0) return ArrowDown
  return Minus
})

// 趋势文本
const trendText = computed(() => {
  if (props.trend === undefined) return ''
  const absValue = Math.abs(props.trend)
  if (props.trend > 0) return `+${absValue}%`
  if (props.trend < 0) return `-${absValue}%`
  return '0%'
})

// 趋势样式类
const trendClass = computed(() => {
  if (props.trend === undefined) return ''
  if (props.trend > 0) return 'trend-up'
  if (props.trend < 0) return 'trend-down'
  return 'trend-flat'
})
</script>

<style scoped>
.stat-card {
  cursor: default;
  transition: all 0.3s;
  border-radius: 8px;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon :deep(.el-icon) {
  font-size: 28px;
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
  font-weight: 500;
}

.stat-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 4px;
}

.value-number {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.value-unit {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.trend-up {
  color: #67c23a;
}

.trend-down {
  color: #f56c6c;
}

.trend-flat {
  color: #909399;
}

.stat-trend :deep(.el-icon) {
  font-size: 14px;
}
</style>
