<template>
  <div class="header-container">
    <!-- Logo和标题 -->
    <div class="header-left">
      <img src="/logo.svg" alt="Logo" class="logo" />
      <h1 class="title">SmartMES Lite</h1>
    </div>

    <!-- 右侧功能区 -->
    <div class="header-right">
      <!-- 语言切换 -->
      <el-dropdown class="language-dropdown" @command="handleLanguageChange">
        <div class="language-button">
          <el-icon :size="18"><Globe /></el-icon>
          <span class="language-text">{{ currentLanguageLabel }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="zh-CN" :class="{ active: currentLanguage === 'zh-CN' }">
              中文简体
            </el-dropdown-item>
            <el-dropdown-item command="en" :class="{ active: currentLanguage === 'en' }">
              English
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

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
            <el-dropdown-item>{{ $t('header.profile') }}</el-dropdown-item>
            <el-dropdown-item>{{ $t('menu.settings') }}</el-dropdown-item>
            <el-dropdown-item divided>{{ $t('header.logout') }}</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Bell, User } from '@element-plus/icons-vue'
import { setLanguage, getLanguage } from '@/locales'

// Globe 图标（Element Plus 没有内置，使用自定义SVG）
const Globe = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
      <path d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm0 18c-4.411 0-8-3.589-8-8s3.589-8 8-8 8 3.589 8 8-3.589 8-8 8zm-1-14h2v2h-2zm0 4h2v6h-2z"/>
      <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/>
    </svg>
  `
}

const { locale } = useI18n()

// 通知数量
const notificationCount = ref(3)

// 当前语言
const currentLanguage = computed(() => getLanguage())

// 当前语言标签
const currentLanguageLabel = computed(() => {
  return currentLanguage.value === 'zh-CN' ? '中文' : 'EN'
})

// 切换语言
const handleLanguageChange = (lang) => {
  setLanguage(lang)
  // 刷新页面以更新 Element Plus 的语言
  window.location.reload()
}
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

.language-dropdown {
  cursor: pointer;
}

.language-button {
  display: flex;
  align-items: center;
  gap: 6px;
  color: white;
  padding: 6px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.language-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.language-text {
  font-size: 14px;
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

:deep(.el-dropdown-menu__item.active) {
  color: #409EFF;
  font-weight: 600;
}
</style>
