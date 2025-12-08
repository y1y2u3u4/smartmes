<template>
  <AppLayout />
</template>

<script setup>
import AppLayout from '@/components/Layout/AppLayout.vue'
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100vh;
}

.app-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* Header Styles */
.app-header {
  background: linear-gradient(135deg, #1e4d8b 0%, #2965b0 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-section h1 {
  color: #fff;
  font-size: 22px;
  font-weight: 600;
  margin: 0;
  letter-spacing: 0.5px;
}

.nav-menu {
  display: flex;
  gap: 8px;
  align-items: center;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.3s;
  font-size: 15px;
  font-weight: 500;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.nav-item.router-link-active {
  background-color: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.nav-item .el-icon {
  font-size: 18px;
}

/* Main Content Styles */
.app-main {
  flex: 1;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 0;
}

/* Footer Styles */
.app-footer {
  background-color: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 16px 24px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-content {
  text-align: center;
}

.footer-content p {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

/* Page Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Scrollbar Styles */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* Responsive Design */
@media (max-width: 768px) {
  .app-header {
    height: auto;
    padding: 12px 0;
  }

  .header-content {
    flex-direction: column;
    gap: 12px;
    padding: 0 12px;
  }

  .logo-section h1 {
    font-size: 18px;
  }

  .nav-menu {
    width: 100%;
    justify-content: space-around;
  }

  .nav-item {
    padding: 8px 12px;
    font-size: 14px;
  }

  .nav-item span {
    display: none;
  }

  .app-footer {
    padding: 12px;
  }

  .footer-content p {
    font-size: 12px;
  }
}
</style>
