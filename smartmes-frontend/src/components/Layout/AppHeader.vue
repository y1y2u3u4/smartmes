<template>
  <div class="header-container">
    <!-- Logo and Title -->
    <div class="header-left">
      <img src="/logo.svg" alt="Logo" class="logo" />
      <h1 class="title">SmartMES Lite</h1>
    </div>

    <!-- Right Actions -->
    <div class="header-right">
      <!-- Notification Icon -->
      <el-badge :value="notificationCount" class="notification-badge">
        <el-icon :size="20" class="icon-button">
          <Bell />
        </el-icon>
      </el-badge>

      <!-- User Menu -->
      <el-dropdown class="user-dropdown" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" class="user-avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="user-details">
            <span class="username">{{ displayName }}</span>
            <span class="user-role">{{ userRole }}</span>
          </div>
          <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile" :icon="UserFilled">
              Profile
            </el-dropdown-item>
            <el-dropdown-item command="password" :icon="Lock">
              Change Password
            </el-dropdown-item>
            <el-dropdown-item command="settings" :icon="Setting">
              Settings
            </el-dropdown-item>
            <el-dropdown-item divided command="logout" :icon="SwitchButton">
              Logout
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>

  <!-- Change Password Dialog -->
  <el-dialog
    v-model="showPasswordDialog"
    title="Change Password"
    width="400px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="passwordFormRef"
      :model="passwordForm"
      :rules="passwordRules"
      label-width="120px"
    >
      <el-form-item label="Old Password" prop="oldPassword">
        <el-input
          v-model="passwordForm.oldPassword"
          type="password"
          show-password
        />
      </el-form-item>
      <el-form-item label="New Password" prop="newPassword">
        <el-input
          v-model="passwordForm.newPassword"
          type="password"
          show-password
        />
      </el-form-item>
      <el-form-item label="Confirm" prop="confirmPassword">
        <el-input
          v-model="passwordForm.confirmPassword"
          type="password"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showPasswordDialog = false">Cancel</el-button>
      <el-button type="primary" :loading="changingPassword" @click="handleChangePassword">
        Confirm
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Bell,
  User,
  UserFilled,
  Lock,
  Setting,
  SwitchButton,
  ArrowDown
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'
import { changePassword } from '@/api/auth'

const router = useRouter()
const authStore = useAuthStore()

// Notification count
const notificationCount = ref(3)

// Computed user info
const displayName = computed(() => authStore.realName || authStore.username || 'User')
const userRole = computed(() => {
  const role = authStore.userRole
  const roleMap = {
    ADMIN: 'Administrator',
    SUPERVISOR: 'Supervisor',
    ENGINEER: 'Engineer',
    OPERATOR: 'Operator'
  }
  return roleMap[role] || role
})

// Password change dialog
const showPasswordDialog = ref(false)
const passwordFormRef = ref(null)
const changingPassword = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: 'Please enter old password', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: 'Please enter new password', trigger: 'blur' },
    { min: 8, message: 'Password must be at least 8 characters', trigger: 'blur' },
    { pattern: /^(?=.*[A-Z])(?=.*\d).+$/, message: 'Must contain uppercase and number', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm password', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// Handle dropdown command
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      // TODO: Navigate to profile page
      ElMessage.info('Profile page coming soon')
      break
    case 'password':
      showPasswordDialog.value = true
      resetPasswordForm()
      break
    case 'settings':
      // TODO: Navigate to settings page
      ElMessage.info('Settings page coming soon')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// Handle logout
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to logout?',
      'Confirm Logout',
      {
        confirmButtonText: 'Logout',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await authStore.logout()
    router.push('/login')
    ElMessage.success('Logged out successfully')
  } catch {
    // User cancelled
  }
}

// Handle password change
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
  } catch {
    return
  }

  changingPassword.value = true

  try {
    const response = await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword
    })

    if (response.code === 200) {
      ElMessage.success('Password changed successfully')
      showPasswordDialog.value = false

      // Optionally logout after password change
      ElMessageBox.confirm(
        'For security, please login again with your new password.',
        'Password Changed',
        {
          confirmButtonText: 'Login Again',
          showCancelButton: false,
          type: 'success'
        }
      ).then(() => {
        authStore.logout()
        router.push('/login')
      })
    } else {
      ElMessage.error(response.message || 'Failed to change password')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || 'Failed to change password')
  } finally {
    changingPassword.value = false
  }
}

// Reset password form
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
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
  gap: 10px;
}

.user-avatar {
  background-color: #3b82f6;
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.username {
  color: white;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.2;
}

.user-role {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  line-height: 1.2;
}

.dropdown-arrow {
  color: white;
  font-size: 12px;
}
</style>
