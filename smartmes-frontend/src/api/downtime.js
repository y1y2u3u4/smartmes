import request from './request'

/**
 * 异常停机上报 API
 */

// 获取设备列表
export const getEquipmentList = () => {
  return request({
    url: '/equipment/list',
    method: 'get'
  })
}

// 提交异常上报
export const submitDowntimeReport = (data) => {
  return request({
    url: '/downtime/report',
    method: 'post',
    data
  })
}

// 上传图片
export const uploadImage = (formData) => {
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取异常列表
export const getDowntimeList = (params) => {
  return request({
    url: '/downtime/list',
    method: 'get',
    params
  })
}

// 响应异常
export const respondDowntime = (id, data) => {
  return request({
    url: `/downtime/${id}/respond`,
    method: 'put',
    data
  })
}

// 解决异常
export const resolveDowntime = (id, data) => {
  return request({
    url: `/downtime/${id}/resolve`,
    method: 'put',
    data
  })
}

// 获取异常详情
export const getDowntimeDetail = (id) => {
  return request({
    url: `/downtime/${id}`,
    method: 'get'
  })
}

// 获取异常类型统计
export const getDowntimeStats = (params) => {
  return request({
    url: '/downtime/stats',
    method: 'get',
    params
  })
}
