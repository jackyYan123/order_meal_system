// utils/orderStatus.js - 订单状态工具类

/**
 * 订单状态映射
 */
const ORDER_STATUS = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED', 
  PREPARING: 'PREPARING',
  READY: 'READY',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED'
}

/**
 * 支付状态映射
 */
const PAYMENT_STATUS = {
  UNPAID: 'UNPAID',
  PAID: 'PAID',
  REFUNDED: 'REFUNDED'
}

/**
 * 获取订单状态文本
 */
function getOrderStatusText(status) {
  const statusMap = {
    [ORDER_STATUS.PENDING]: '待确认',
    [ORDER_STATUS.CONFIRMED]: '已确认',
    [ORDER_STATUS.PREPARING]: '制作中',
    [ORDER_STATUS.READY]: '待取餐',
    [ORDER_STATUS.COMPLETED]: '已完成',
    [ORDER_STATUS.CANCELLED]: '已取消'
  }
  return statusMap[status] || status
}

/**
 * 获取支付状态文本
 */
function getPaymentStatusText(status) {
  const statusMap = {
    [PAYMENT_STATUS.UNPAID]: '未支付',
    [PAYMENT_STATUS.PAID]: '已支付',
    [PAYMENT_STATUS.REFUNDED]: '已退款'
  }
  return statusMap[status] || status
}

/**
 * 获取订单状态颜色
 */
function getOrderStatusColor(status) {
  const colorMap = {
    [ORDER_STATUS.PENDING]: '#ff9500',
    [ORDER_STATUS.CONFIRMED]: '#007aff',
    [ORDER_STATUS.PREPARING]: '#ff6b35',
    [ORDER_STATUS.READY]: '#34c759',
    [ORDER_STATUS.COMPLETED]: '#8e8e93',
    [ORDER_STATUS.CANCELLED]: '#ff3b30'
  }
  return colorMap[status] || '#8e8e93'
}

/**
 * 判断是否可以支付
 */
function canPay(order) {
  return order && 
         order.paymentStatus === PAYMENT_STATUS.UNPAID && 
         (order.status === ORDER_STATUS.PENDING || order.status === ORDER_STATUS.CONFIRMED)
}

/**
 * 判断是否可以取消
 */
function canCancel(order) {
  return order && 
         (order.status === ORDER_STATUS.PENDING || order.status === ORDER_STATUS.CONFIRMED) &&
         order.paymentStatus === PAYMENT_STATUS.UNPAID
}

/**
 * 判断订单是否进行中
 */
function isOrderActive(order) {
  return order && [
    ORDER_STATUS.PENDING,
    ORDER_STATUS.CONFIRMED,
    ORDER_STATUS.PREPARING,
    ORDER_STATUS.READY
  ].includes(order.status)
}

/**
 * 判断订单是否已完成
 */
function isOrderCompleted(order) {
  return order && order.status === ORDER_STATUS.COMPLETED
}

/**
 * 判断订单是否已取消
 */
function isOrderCancelled(order) {
  return order && order.status === ORDER_STATUS.CANCELLED
}

module.exports = {
  ORDER_STATUS,
  PAYMENT_STATUS,
  getOrderStatusText,
  getPaymentStatusText,
  getOrderStatusColor,
  canPay,
  canCancel,
  isOrderActive,
  isOrderCompleted,
  isOrderCancelled
}