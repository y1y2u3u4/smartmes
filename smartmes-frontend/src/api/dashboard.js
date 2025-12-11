import request from './request'

/**
 * 数据看板 API
 * 后端返回格式: { code: 200, message: "Success", data: {...} }
 */

// 获取生产概览数据（包含今日工单统计和产量统计）
export const getTodayOrderStats = async () => {
  const response = await request({
    url: '/dashboard/overview',
    method: 'get'
  })
  // 转换为前端期望的格式
  const data = response.data || {}
  return {
    total: data.todayWorkOrderTotal || 0,
    completed: data.todayCompleted || 0,
    inProgress: data.todayInProgress || 0,
    exception: data.todayAbnormal || 0,
    trends: {
      total: 0,
      completed: 0,
      inProgress: 0,
      exception: 0
    }
  }
}

// 获取今日产量完成率（使用overview接口）
export const getTodayProductionRate = async () => {
  const response = await request({
    url: '/dashboard/overview',
    method: 'get'
  })
  const data = response.data || {}
  return {
    target: data.planQtyTotal || 0,
    actual: data.actualQtyTotal || 0,
    percentage: data.completionRate || 0
  }
}

// 获取设备状态分布
export const getEquipmentStatusDistribution = async () => {
  const response = await request({
    url: '/dashboard/equipment-status',
    method: 'get'
  })
  const data = response.data || {}
  // 转换为图表需要的数组格式
  return [
    { name: 'Running', value: data.runningCount || 0 },
    { name: 'Idle', value: data.idleCount || 0 },
    { name: 'Maintenance', value: data.maintenanceCount || 0 },
    { name: 'Fault', value: data.faultCount || 0 }
  ].filter(item => item.value > 0)
}

// 获取异常类型分布
export const getDowntimeTypeDistribution = async () => {
  const response = await request({
    url: '/dashboard/downtime-stats',
    method: 'get'
  })
  const data = response.data || {}
  const downtimeByType = data.downtimeByType || {}
  // 转换为图表需要的数组格式
  const typeLabels = {
    'EQUIPMENT_FAULT': 'Equipment Fault',
    'QUALITY_ISSUE': 'Quality Issue',
    'MATERIAL_SHORTAGE': 'Material Shortage',
    'TOOL_CHANGE': 'Tool Change',
    'OTHER': 'Other'
  }
  return Object.entries(downtimeByType).map(([key, value]) => ({
    name: typeLabels[key] || key,
    value: value
  }))
}

// 获取设备故障TOP5
export const getEquipmentFailureTop5 = async () => {
  const response = await request({
    url: '/dashboard/downtime-stats',
    method: 'get'
  })
  const data = response.data || {}
  const topFaultyEquipment = data.topFaultyEquipment || []
  // 转换为图表需要的数组格式
  return topFaultyEquipment.map(item => ({
    name: item.equipmentName || item.equipmentId || 'Unknown',
    value: item.count || 0
  })).slice(0, 5)
}

// 获取看板全量数据（一次性获取所有数据）
export const getDashboardData = async () => {
  const response = await request({
    url: '/dashboard/complete',
    method: 'get'
  })
  return response.data
}
