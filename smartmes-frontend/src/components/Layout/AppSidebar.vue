<template>
  <div class="sidebar-container">
    <!-- 折叠按钮 -->
    <div class="collapse-button" @click="handleToggleCollapse">
      <el-icon :size="20">
        <Fold v-if="!isCollapse" />
        <Expand v-else />
      </el-icon>
    </div>

    <!-- 菜单 -->
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :unique-opened="true"
      router
      class="sidebar-menu"
    >
      <el-menu-item index="/workorders">
        <el-icon><Document /></el-icon>
        <template #title>Work Orders</template>
      </el-menu-item>

      <el-menu-item index="/dashboard">
        <el-icon><DataLine /></el-icon>
        <template #title>Dashboard</template>
      </el-menu-item>

      <el-sub-menu index="/production">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>Production</span>
        </template>
        <el-menu-item index="/production/lines">Production Lines</el-menu-item>
        <el-menu-item index="/production/equipment">Equipment</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="/quality">
        <template #title>
          <el-icon><Check /></el-icon>
          <span>Quality</span>
        </template>
        <el-menu-item index="/quality/inspection">Inspection</el-menu-item>
        <el-menu-item index="/quality/defects">Defects</el-menu-item>
      </el-sub-menu>

      <el-menu-item index="/reports">
        <el-icon><TrendCharts /></el-icon>
        <template #title>Reports</template>
      </el-menu-item>

      <el-menu-item index="/settings">
        <el-icon><Tools /></el-icon>
        <template #title>Settings</template>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  Document,
  DataLine,
  Setting,
  Check,
  TrendCharts,
  Tools,
  Fold,
  Expand
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['toggle-collapse'])

// 当前路由
const route = useRoute()

// 激活的菜单项
const activeMenu = computed(() => {
  return route.path
})

// 切换折叠
const handleToggleCollapse = () => {
  emit('toggle-collapse')
}
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.collapse-button {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-bottom: 1px solid #e4e7ed;
  transition: background-color 0.3s;
}

.collapse-button:hover {
  background-color: #e4e7ed;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 200px;
}
</style>
