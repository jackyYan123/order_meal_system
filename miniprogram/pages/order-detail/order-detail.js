// pages/order-detail/order-detail.js
const app = getApp()
const { getOrderDetail, getOrderByOrderNo } = require('../../api/order')
const { formatTime, formatCurrency } = require('../../utils/util')

Page({
  data: {
    orderId: null,
    orderNo: null,
    orderInfo: null,
    loading: true
  },

  onLoad(options) {
    console.log('订单详情页面参数：', options)
    
    if (options.orderId) {
      this.setData({ orderId: options.orderId })
      this.loadOrderDetail()
    } else if (options.orderNo) {
      // 如果传入的是订单号，使用订单号查询
      this.setData({ orderNo: options.orderNo })
      this.loadOrderDetailByOrderNo()
    } else {
      // 尝试从全局数据获取订单信息
      const orderInfo = app.globalData.orderInfo
      if (orderInfo) {
        console.log('从全局数据获取订单信息：', orderInfo)
        const formattedOrderInfo = this.formatOrderData(orderInfo)
        this.setData({
          orderInfo: formattedOrderInfo,
          loading: false
        })
      } else {
        this.setData({ loading: false })
        wx.showToast({
          title: '订单信息不存在',
          icon: 'none'
        })
      }
    }
  },

  // 加载订单详情
  async loadOrderDetail() {
    try {
      this.setData({ loading: true })
      
      console.log('查询订单ID：', this.data.orderId)
      const res = await getOrderDetail(this.data.orderId)
      console.log('订单详情响应：', res)
      
      if (res.success) {
        const formattedOrderInfo = this.formatOrderData(res.data)
        this.setData({
          orderInfo: formattedOrderInfo,
          loading: false
        })
      } else {
        this.setData({ loading: false })
        wx.showToast({
          title: res.message || '查询失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('加载订单详情失败：', error)
      this.setData({ loading: false })
      
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    }
  },

  // 根据订单号加载订单详情
  async loadOrderDetailByOrderNo() {
    try {
      this.setData({ loading: true })
      
      console.log('查询订单号：', this.data.orderNo)
      const res = await getOrderByOrderNo(this.data.orderNo)
      console.log('订单详情响应：', res)
      
      if (res.success) {
        const formattedOrderInfo = this.formatOrderData(res.data)
        this.setData({
          orderInfo: formattedOrderInfo,
          loading: false
        })
      } else {
        this.setData({ loading: false })
        wx.showToast({
          title: res.message || '查询失败',
          icon: 'none'
        })
      }
    } catch (error) {
      console.error('根据订单号加载订单详情失败：', error)
      this.setData({ loading: false })
      
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    }
  },

  // 返回首页
  goHome() {
    wx.switchTab({
      url: '/pages/index/index'
    })
  },

  // 查看菜单
  goMenu() {
    wx.switchTab({
      url: '/pages/menu/menu'
    })
  },

  // 格式化时间
  formatTime(time) {
    return formatTime(time)
  },

  // 格式化价格
  formatPrice(price) {
    if (price === null || price === undefined || price === '') {
      return '¥0.00'
    }
    // 确保转换为数字类型
    const numPrice = parseFloat(price)
    if (isNaN(numPrice)) {
      return '¥0.00'
    }
    return '¥' + numPrice.toFixed(2)
  },

  // 格式化订单数据
  formatOrderData(orderInfo) {
    if (!orderInfo) return null
    
    return {
      ...orderInfo,
      formattedCreatedAt: formatTime(orderInfo.createdAt),
      formattedTotalAmount: this.formatPrice(orderInfo.totalAmount),
      items: orderInfo.items ? orderInfo.items.map(item => ({
        ...item,
        formattedDishPrice: this.formatPrice(item.dishPrice),
        formattedSubtotal: this.formatPrice(item.subtotal)
      })) : []
    }
  },

  // 获取订单状态文本
  getStatusText(status) {
    const statusMap = {
      'PENDING': '待确认',
      'CONFIRMED': '已确认',
      'PREPARING': '制作中',
      'READY': '待取餐',
      'COMPLETED': '已完成',
      'CANCELLED': '已取消'
    }
    return statusMap[status] || status
  },

  // 获取订单状态颜色
  getStatusColor(status) {
    const colorMap = {
      'PENDING': '#ff9500',
      'CONFIRMED': '#007aff',
      'PREPARING': '#ff6b35',
      'READY': '#34c759',
      'COMPLETED': '#8e8e93',
      'CANCELLED': '#ff3b30'
    }
    return colorMap[status] || '#8e8e93'
  }
})