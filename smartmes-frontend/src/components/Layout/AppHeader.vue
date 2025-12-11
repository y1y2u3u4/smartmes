<template>
  <div class="header-container">
    <!-- Logo和标题 -->
    <div class="header-left">
      <img src="/logo.svg" alt="Logo" class="logo" />
      <h1 class="title">SmartMES Lite</h1>
    </div>

    <!-- 右侧功能区 -->
    <div class="header-right">
      <!-- 通知图标 -->
      <el-badge :value="notificationCount" class="notification-badge">
        <el-icon :size="20" class="icon-button">
          <Bell />
        </el-icon>
      </el-badge>

      <!-- 用户菜单 -->
      <el-dropdown class="user-dropdown">
        <div class="user-info">
          <el-avatar :size="32" class="user-avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <span class="username">Admin</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>Profile</el-dropdown-item>
            <el-dropdown-item>Settings</el-dropdown-item>
            <el-dropdown-item divided>Logout</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Bell, User } from '@element-plus/icons-vue'

// 通知数量
const notificationCount = ref(3)
</script>

<style scoped>
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 36px;
  width: 36px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: white;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-badge {
  cursor: pointer;
}

.icon-button {
  color: white;
  cursor: pointer;
  transition: opacity 0.3s;
}

.icon-button:hover {
  opacity: 0.8;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  background-color: #3b82f6;
}

.username {
  color: white;
  font-size: 14px;
}
</style>
