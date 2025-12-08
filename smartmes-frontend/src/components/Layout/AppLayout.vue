<template>
  <el-container class="app-container">
    <!-- 顶部导航栏 -->
    <el-header class="app-header">
      <AppHeader />
    </el-header>

    <el-container>
      <!-- 左侧菜单栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="app-aside">
        <AppSidebar :is-collapse="isCollapse" @toggle-collapse="toggleCollapse" />
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="app-main">
        <!-- 面包屑导航 -->
        <el-breadcrumb separator="/" class="breadcrumb">
          <el-breadcrumb-item :to="{ path: '/' }">Home</el-breadcrumb-item>
          <el-breadcrumb-item
            v-for="(item, index) in breadcrumbs"
            :key="index"
          >
            {{ item }}
          </el-breadcrumb-item>
        </el-breadcrumb>

        <!-- 路由视图 -->
        <div class="content-wrapper">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'

// 侧边栏折叠状态
const isCollapse = ref(false)

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 获取面包屑
const route = useRoute()
const breadcrumbs = computed(() => {
  return route.meta.breadcrumb || []
})
</script>

<style scoped>
.app-container {
  height: 100vh;
}

.app-header {
  background-color: #1E3A8A;
  color: white;
  padding: 0;
  height: 60px;
}

.app-aside {
  background-color: #f5f7fa;
  transition: width 0.3s;
  overflow: hidden;
}

.app-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.breadcrumb {
  margin-bottom: 20px;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
}

.content-wrapper {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  min-height: calc(100vh - 140px);
}
</style>
