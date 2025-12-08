/**
 * 工单API接口
 */
import request from './request'
import { API_PATHS } from './config'

/**
 * 获取工单列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.page_size - 每页数量
 * @param {string} params.search - 搜索关键词
 * @param {string} params.status - 工单状态
 * @returns {Promise}
 */
export const getWorkOrderList = (params) => {
  return request({
    url: API_PATHS.WORKORDER.LIST,
    method: 'GET',
    params
  })
}

/**
 * 获取工单详情
 * @param {string|number} id - 工单ID
 * @returns {Promise}
 */
export const getWorkOrderDetail = (id) => {
  return request({
    url: API_PATHS.WORKORDER.DETAIL(id),
    method: 'GET'
  })
}

/**
 * 创建工单
 * @param {Object} data - 工单数据
 * @param {string} data.work_order_number - 工单号
 * @param {string} data.product_code - 产品编码
 * @param {string} data.batch_number - 批次号
 * @param {number} data.planned_quantity - 计划数量
 * @param {string} data.production_line - 产线
 * @param {string} data.equipment - 设备
 * @param {string} data.operator - 操作员
 * @returns {Promise}
 */
export const createWorkOrder = (data) => {
  return request({
    url: API_PATHS.WORKORDER.CREATE,
    method: 'POST',
    data
  })
}

/**
 * 更新工单
 * @param {string|number} id - 工单ID
 * @param {Object} data - 工单数据
 * @returns {Promise}
 */
export const updateWorkOrder = (id, data) => {
  return request({
    url: API_PATHS.WORKORDER.UPDATE(id),
    method: 'PUT',
    data
  })
}

/**
 * 删除工单
 * @param {string|number} id - 工单ID
 * @returns {Promise}
 */
export const deleteWorkOrder = (id) => {
  return request({
    url: API_PATHS.WORKORDER.DELETE(id),
    method: 'DELETE'
  })
}

/**
 * 开始工单
 * @param {string|number} id - 工单ID
 * @returns {Promise}
 */
export const startWorkOrder = (id) => {
  return request({
    url: API_PATHS.WORKORDER.START(id),
    method: 'POST'
  })
}

/**
 * 完成工单
 * @param {string|number} id - 工单ID
 * @param {Object} data - 完成数据
 * @param {number} data.actual_quantity - 实际完成数量
 * @returns {Promise}
 */
export const completeWorkOrder = (id, data) => {
  return request({
    url: API_PATHS.WORKORDER.COMPLETE(id),
    method: 'POST',
    data
  })
}

/**
 * 获取产品列表（用于下拉选择）
 * @returns {Promise}
 */
export const getProductList = () => {
  return request({
    url: API_PATHS.PRODUCT.LIST,
    method: 'GET'
  })
}

/**
 * 获取设备列表（用于下拉选择）
 * @returns {Promise}
 */
export const getEquipmentList = () => {
  return request({
    url: API_PATHS.EQUIPMENT.LIST,
    method: 'GET'
  })
}

/**
 * 获取操作员列表（用于下拉选择）
 * @returns {Promise}
 */
export const getOperatorList = () => {
  return request({
    url: API_PATHS.OPERATOR.LIST,
    method: 'GET'
  })
}
