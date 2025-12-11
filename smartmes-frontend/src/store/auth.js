import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, logout as apiLogout, getCurrentUser, refreshToken } from '@/api/auth'

// Token storage keys
const ACCESS_TOKEN_KEY = 'access_token'
const REFRESH_TOKEN_KEY = 'refresh_token'
const USER_KEY = 'user_info'

export const useAuthStore = defineStore('auth', () => {
  // State
  const accessToken = ref(localStorage.getItem(ACCESS_TOKEN_KEY) || '')
  const refreshTokenValue = ref(localStorage.getItem(REFRESH_TOKEN_KEY) || '')
  const user = ref(JSON.parse(localStorage.getItem(USER_KEY) || 'null'))
  const loading = ref(false)

  // Getters
  const isAuthenticated = computed(() => !!accessToken.value)
  const userRole = computed(() => user.value?.role || '')
  const username = computed(() => user.value?.username || '')
  const realName = computed(() => user.value?.realName || '')

  // Actions
  /**
   * Login with username and password
   */
  async function login(credentials) {
    loading.value = true
    try {
      const response = await apiLogin(credentials)
      if (response.code === 200) {
        const data = response.data
        setTokens(data.accessToken, data.refreshToken)
        setUser(data.user)
        return { success: true }
      }
      return { success: false, message: response.message }
    } catch (error) {
      console.error('Login failed:', error)
      return {
        success: false,
        message: error.response?.data?.message || 'Login failed'
      }
    } finally {
      loading.value = false
    }
  }

  /**
   * Logout current user
   */
  async function logout() {
    try {
      await apiLogout()
    } catch (error) {
      console.error('Logout API call failed:', error)
    } finally {
      clearAuth()
    }
  }

  /**
   * Refresh access token
   */
  async function refresh() {
    if (!refreshTokenValue.value) {
      return false
    }
    try {
      const response = await refreshToken(refreshTokenValue.value)
      if (response.code === 200) {
        accessToken.value = response.data.accessToken
        localStorage.setItem(ACCESS_TOKEN_KEY, response.data.accessToken)
        return true
      }
      return false
    } catch (error) {
      console.error('Token refresh failed:', error)
      clearAuth()
      return false
    }
  }

  /**
   * Fetch current user info
   */
  async function fetchCurrentUser() {
    try {
      const response = await getCurrentUser()
      if (response.code === 200) {
        setUser(response.data)
        return true
      }
      return false
    } catch (error) {
      console.error('Failed to fetch user:', error)
      return false
    }
  }

  /**
   * Check if user has specific role
   */
  function hasRole(role) {
    return userRole.value === role
  }

  /**
   * Check if user has any of the specified roles
   */
  function hasAnyRole(roles) {
    return roles.includes(userRole.value)
  }

  // Helper functions
  function setTokens(access, refresh) {
    accessToken.value = access
    refreshTokenValue.value = refresh
    localStorage.setItem(ACCESS_TOKEN_KEY, access)
    localStorage.setItem(REFRESH_TOKEN_KEY, refresh)
  }

  function setUser(userInfo) {
    user.value = userInfo
    localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
  }

  function clearAuth() {
    accessToken.value = ''
    refreshTokenValue.value = ''
    user.value = null
    localStorage.removeItem(ACCESS_TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  function getAccessToken() {
    return accessToken.value
  }

  return {
    // State
    accessToken,
    refreshTokenValue,
    user,
    loading,
    // Getters
    isAuthenticated,
    userRole,
    username,
    realName,
    // Actions
    login,
    logout,
    refresh,
    fetchCurrentUser,
    hasRole,
    hasAnyRole,
    getAccessToken,
    clearAuth
  }
})
