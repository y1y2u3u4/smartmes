import request from './request'

/**
 * 数据看板 API
 */

// 获取今日工单统计
export const getTodayOrderStats = () => {
  return request({
    url: '/dashboard/order-stats',
    method: 'get'
  })
}

// 获取今日产量完成率
export const getTodayProductionRate = () => {
  return request({
    url: '/dashboard/production-rate',
    method: 'get'
  })
}

// 获取设备状态分布
export const getEquipmentStatusDistribution = () => {
  return request({
    url: '/dashboard/equipment-status',
    method: 'get'
  })
}

// 获取异常类型分布
export const getDowntimeTypeDistribution = () => {
  return request({
    url: '/dashboard/downtime-distribution',
    method: 'get'
  })
}

// 获取设备故障TOP5
export const getEquipmentFailureTop5 = () => {
  return request({
    url: '/dashboard/equipment-failure-top5',
    method: 'get'
  })
}

// 获取看板全量数据（一次性获取所有数据）
export const getDashboardData = () => {
  return request({
    url: '/dashboard/all',
    method: 'get'
  })
}
