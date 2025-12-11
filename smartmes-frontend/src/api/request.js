/**
 * Axios封装及拦截器配置
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { BASE_URL, TIMEOUT, STATUS_CODE } from './config'
import router from '@/router'

// Token storage keys
const ACCESS_TOKEN_KEY = 'access_token'
const REFRESH_TOKEN_KEY = 'refresh_token'

// Flag to prevent multiple refresh attempts
let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

// 创建axios实例
const request = axios.create({
  baseURL: BASE_URL,
  timeout: TIMEOUT,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // Add token to headers if available
    const token = localStorage.getItem(ACCESS_TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data, status } = response

    // 成功响应直接返回数据
    if (status === STATUS_CODE.SUCCESS || status === STATUS_CODE.CREATED) {
      return data
    }

    // 其他状态码处理
    ElMessage.error(data.message || 'Request failed')
    return Promise.reject(new Error(data.message || 'Request failed'))
  },
  async (error) => {
    const originalRequest = error.config

    // Handle 401 Unauthorized
    if (error.response?.status === STATUS_CODE.UNAUTHORIZED && !originalRequest._retry) {
      // Skip refresh for login and refresh endpoints
      if (originalRequest.url.includes('/auth/login') || originalRequest.url.includes('/auth/refresh')) {
        return Promise.reject(error)
      }

      if (isRefreshing) {
        // Queue requests while refreshing
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(token => {
          originalRequest.headers.Authorization = `Bearer ${token}`
          return request(originalRequest)
        }).catch(err => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)

      if (!refreshToken) {
        // No refresh token, redirect to login
        clearAuthAndRedirect()
        return Promise.reject(error)
      }

      try {
        // Attempt to refresh token
        const response = await axios.post(`${BASE_URL}/auth/refresh`, {
          refreshToken
        })

        if (response.data.code === 200) {
          const newAccessToken = response.data.data.accessToken
          localStorage.setItem(ACCESS_TOKEN_KEY, newAccessToken)

          // Update authorization header
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`

          // Process queued requests
          processQueue(null, newAccessToken)

          return request(originalRequest)
        } else {
          processQueue(new Error('Token refresh failed'), null)
          clearAuthAndRedirect()
          return Promise.reject(error)
        }
      } catch (refreshError) {
        processQueue(refreshError, null)
        clearAuthAndRedirect()
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    // Handle other errors
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case STATUS_CODE.BAD_REQUEST:
          ElMessage.error(data.message || 'Invalid request')
          break
        case STATUS_CODE.UNAUTHORIZED:
          ElMessage.error('Session expired, please login again')
          clearAuthAndRedirect()
          break
        case STATUS_CODE.FORBIDDEN:
          ElMessage.error('Access denied')
          break
        case STATUS_CODE.NOT_FOUND:
          ElMessage.error('Resource not found')
          break
        case STATUS_CODE.SERVER_ERROR:
          ElMessage.error('Server error, please try again later')
          break
        default:
          ElMessage.error(data.message || 'Request failed')
      }
    } else if (error.request) {
      ElMessage.error('Network error, please check your connection')
    } else {
      ElMessage.error(error.message || 'Request failed')
    }

    return Promise.reject(error)
  }
)

/**
 * Clear auth data and redirect to login
 */
function clearAuthAndRedirect() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
  localStorage.removeItem('user_info')

  // Redirect to login if not already there
  if (router.currentRoute.value.path !== '/login') {
    router.push('/login')
  }
}

export default request
