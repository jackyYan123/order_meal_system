// pages/payment-result/payment-result.js
const app = getApp()
const { getOrderDetail, getOrderByOrderNo } = require('../../api/order')
// 移除时间格式化相关的导入

Page({
  data: {
    orderId: null,
    orderNo: null,
    paymentId: null,
    status: 'success', // success, failed
    message: '',
    order: null,
    loading: true
  },

  onLoad(options) {
    const { orderId, orderNo, paymentId, status, message } = options
    
    console.log('支付结果页面参数：', options)
    
    this.setData({
      orderId: orderId,
      orderNo: orderNo,
      paymentId: paymentId,
      status: status || 'success',
      message: message ? decodeURIComponent(message) : ''
    })
    
    if (orderId) {
      // 延迟加载订单详情，确保支付状态已更新
      setTimeout(() => {
        this.loadOrderDetail()
      }, 1000)
    } else if (orderNo) {
      // 使用订单号加载订单详情
      setTimeout(() => {
        this.loadOrderDetailByOrderNo()
      }, 1000)
    } else {
      this.setData({ loading: false })
    }
  },

  // 加载订单详情
  async loadOrderDetail() {
    if (!this.data.orderId) return
    
    try {
      const result = await getOrderDetail(this.data.orderId)
      
      if (result.success) {
        this.setData({
          order: result.data,
          loading: false
        })
      } else {
        throw new Error(result.message)
      }
    } catch (error) {
      console.error('加载订单详情失败:', error)
      this.setData({ loading: false })
    }
  },

  // 根据订单号加载订单详情
  async loadOrderDetailByOrderNo() {
    if (!this.data.orderNo) return
    
    try {
      console.log('查询订单号：', this.data.orderNo)
      const result = await getOrderByOrderNo(this.data.orderNo)
      
      if (result.success) {
        this.setData({
          order: result.data,
          loading: false
        })
      } else {
        throw new Error(result.message)
      }
    } catch (error) {
      console.error('根据订单号加载订单详情失败:', error)
      this.setData({ loading: false })
    }
  },

  // 查看订单详情
  viewOrderDetail() {
    // 优先使用订单号，如果没有订单号则使用订单ID
    if (this.data.orderNo) {
      wx.redirectTo({
        url: `/pages/order-detail/order-detail?orderNo=${this.data.orderNo}`
      })
    } else if (this.data.orderId) {
      wx.redirectTo({
        url: `/pages/order-detail/order-detail?orderId=${this.data.orderId}`
      })
    }
  },

  // 返回首页
  goToHome() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  },

  // 查看订单列表
  goToOrders() {
    wx.switchTab({
      url: '/pages/order/order'
    })
  },

  // 继续购物
  continueShopping() {
    wx.switchTab({
      url: '/pages/menu/menu'
    })
  },

  // 联系客服
  contactService() {
    wx.makePhoneCall({
      phoneNumber: '400-123-4567'
    })
  },

  // 格式化价格
  formatPrice(price) {
    if (price === null || price === undefined || price === '') {
      return '¥0.00'
    }
    const numPrice = parseFloat(price)
    if (isNaN(numPrice)) {
      return '¥0.00'
    }
    return '¥' + numPrice.toFixed(2)
  }
})