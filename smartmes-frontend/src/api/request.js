/**
 * Axios封装及拦截器配置
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { BASE_URL, TIMEOUT, STATUS_CODE } from './config'

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
    // 可以在这里添加token等认证信息
    // const token = localStorage.getItem('token')
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`
    // }
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
  (error) => {
    // 错误响应处理
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case STATUS_CODE.BAD_REQUEST:
          ElMessage.error(data.message || 'Invalid request')
          break
        case STATUS_CODE.UNAUTHORIZED:
          ElMessage.error('Unauthorized, please login')
          // 可以在这里跳转到登录页
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

export default request
