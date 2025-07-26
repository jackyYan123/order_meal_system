// api/order.js - 订单相关API
const { get, post, put } = require('../utils/request')

/**
 * 创建订单
 * @param {Object} orderData - 订单数据
 */
function createOrder(orderData) {
  return post('/api/orders', orderData)
}

/**
 * 获取订单详情
 * @param {number} orderId - 订单ID
 */
function getOrderDetail(orderId) {
  return get(`/api/orders/${orderId}`)
}

/**
 * 根据订单号获取订单详情
 * @param {string} orderNo - 订单号
 */
function getOrderByOrderNo(orderNo) {
  return get(`/api/orders/orderNo/${orderNo}`)
}

/**
 * 获取用户订单列表
 * @param {Object} params - 查询参数
 */
function getOrderList(params = {}) {
  return get('/api/orders', params)
}

/**
 * 取消订单
 * @param {number} orderId - 订单ID
 * @param {string} reason - 取消原因
 */
function cancelOrder(orderId, reason) {
  return put(`/api/orders/${orderId}/cancel`, { reason })
}

/**
 * 获取桌台进行中的订单
 * @param {number} tableId - 桌台ID
 */
function getActiveOrdersByTable(tableId) {
  return get(`/api/orders/table/${tableId}/active`)
}

module.exports = {
  createOrder,
  getOrderDetail,
  getOrderByOrderNo,
  getOrderList,
  cancelOrder,
  getActiveOrdersByTable
}