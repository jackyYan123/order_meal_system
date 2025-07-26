// pages/payment-result/payment-result.js
const app = getApp()
const { getOrderDetail } = require('../../api/order')
const { formatTime, formatPrice } = require('../../utils/util')

Page({
  data: {
    orderId: null,
    paymentId: null,
    status: 'success', // success, failed
    message: '',
    order: null,
    loading: true
  },

  onLoad(options) {
    const { orderId, paymentId, status, message } = options
    
    this.setData({
      orderId: orderId,
      paymentId: paymentId,
      status: status || 'success',
      message: message ? decodeURIComponent(message) : ''
    })
    
    if (orderId) {
      this.loadOrderDetail()
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

  // 查看订单详情
  viewOrderDetail() {
    if (!this.data.orderId) return
    
    wx.redirectTo({
      url: `/pages/order-detail/order-detail?orderId=${this.data.orderId}`
    })
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

  // 格式化时间
  formatTime,
  
  // 格式化价格
  formatPrice
})