/**
 * API基础配置
 */

// API基础URL
export const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

// 请求超时时间（毫秒）
export const TIMEOUT = 15000

// API路径配置
export const API_PATHS = {
  // 工单相关
  WORKORDER: {
    LIST: '/workorders',
    DETAIL: (id) => `/workorders/${id}`,
    CREATE: '/workorders',
    UPDATE: (id) => `/workorders/${id}`,
    DELETE: (id) => `/workorders/${id}`,
    START: (id) => `/workorders/${id}/start`,
    COMPLETE: (id) => `/workorders/${id}/complete`
  },

  // 产品相关
  PRODUCT: {
    LIST: '/products',
    DETAIL: (id) => `/products/${id}`
  },

  // 设备相关
  EQUIPMENT: {
    LIST: '/equipment',
    DETAIL: (id) => `/equipment/${id}`
  },

  // 操作员相关
  OPERATOR: {
    LIST: '/operators',
    DETAIL: (id) => `/operators/${id}`
  }
}

// 状态码配置
export const STATUS_CODE = {
  SUCCESS: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500
}
