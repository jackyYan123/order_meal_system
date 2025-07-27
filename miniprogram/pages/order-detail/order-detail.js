// pages/order-detail/order-detail.js
const app = getApp()
const { getOrderDetail, getOrderByOrderNo } = require('../../api/order')
// 移除时间格式化相关的导入

Page({
  data: {
    orderId: null,
    orderNo: null,
    orderInfo: null,
    loading: true,
    showPayment: false
  },

  onLoad(options) {
    console.log('订单详情页面参数：', options)
    
    // 检查是否需要显示支付引导
    if (options.showPayment === 'true') {
      this.setData({ showPayment: true })
    }
    
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
        
        // 如果需要显示支付引导
        if (this.data.showPayment) {
          this.showPaymentGuide()
        }
        
        // 检查支付引导
        this.checkPaymentGuide(formattedOrderInfo)
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
      formattedTotalAmount: this.formatPrice(orderInfo.totalAmount),
      formattedPaidAmount: this.formatPrice(orderInfo.paidAmount),
      statusText: this.getStatusText(orderInfo.status),
      statusColor: this.getStatusColor(orderInfo.status),
      paymentStatusText: this.getPaymentStatusText(orderInfo.paymentStatus),
      paymentStatusColor: this.getPaymentStatusColor(orderInfo.paymentStatus),
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
  },

  // 检查是否需要显示支付引导
  checkPaymentGuide(orderInfo) {
    if (this.data.showPayment && orderInfo && 
        orderInfo.paymentStatus === 'UNPAID' && 
        orderInfo.status === 'PENDING') {
      setTimeout(() => {
        this.showPaymentGuide()
      }, 500)
    }
  },

  // 显示支付引导
  showPaymentGuide() {
    if (!this.data.orderInfo) return
    
    wx.showModal({
      title: '订单创建成功',
      content: '请完成支付以确认订单，超时未支付订单将自动取消',
      confirmText: '立即支付',
      cancelText: '稍后支付',
      success: (res) => {
        if (res.confirm) {
          this.goToPayment()
        }
      }
    })
  },

  // 跳转到支付页面
  goToPayment() {
    if (!this.data.orderInfo) return
    
    wx.navigateTo({
      url: `/pages/payment/payment?orderNo=${this.data.orderInfo.orderNo}`
    })
  },

  // 取消订单
  cancelOrder() {
    if (!this.data.orderInfo) return
    
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '取消中...' })
            
            const { cancelOrder } = require('../../api/order')
            const result = await cancelOrder(this.data.orderInfo.id, '用户主动取消')
            
            if (result.success) {
              wx.showToast({
                title: '订单已取消',
                icon: 'success'
              })
              
              // 更新订单状态
              const updatedOrderInfo = {
                ...this.data.orderInfo,
                status: 'CANCELLED'
              }
              
              this.setData({ orderInfo: updatedOrderInfo })
            } else {
              throw new Error(result.message)
            }
          } catch (error) {
            console.error('取消订单失败:', error)
            wx.showToast({
              title: error.message || '取消失败',
              icon: 'none'
            })
          } finally {
            wx.hideLoading()
          }
        }
      }
    })
  },

  // 刷新订单状态
  async refreshOrder() {
    if (!this.data.orderInfo) return
    
    try {
      wx.showLoading({ title: '刷新中...' })
      
      const res = this.data.orderId 
        ? await getOrderDetail(this.data.orderId)
        : await getOrderByOrderNo(this.data.orderNo || this.data.orderInfo.orderNo)
      
      if (res.success) {
        const formattedOrderInfo = this.formatOrderData(res.data)
        this.setData({ orderInfo: formattedOrderInfo })
        
        wx.showToast({
          title: '刷新成功',
          icon: 'success'
        })
      } else {
        throw new Error(res.message)
      }
    } catch (error) {
      console.error('刷新订单失败:', error)
      wx.showToast({
        title: error.message || '刷新失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 获取支付状态文本
  getPaymentStatusText(paymentStatus) {
    const statusMap = {
      'UNPAID': '未支付',
      'PAID': '已支付',
      'REFUNDED': '已退款',
      'REFUNDING': '退款中'
    }
    return statusMap[paymentStatus] || paymentStatus
  },

  // 获取支付状态颜色
  getPaymentStatusColor(paymentStatus) {
    const colorMap = {
      'UNPAID': '#ff9500',     // 橙色 - 未支付
      'PAID': '#34c759',       // 绿色 - 已支付
      'REFUNDED': '#8e8e93',   // 灰色 - 已退款
      'REFUNDING': '#ff9500'   // 橙色 - 退款中
    }
    return colorMap[paymentStatus] || '#8e8e93'
  },



  // 判断是否可以支付
  canPay() {
    const order = this.data.orderInfo
    console.log('canPay检查:', {
      hasOrder: !!order,
      paymentStatus: order?.paymentStatus,
      status: order?.status,
      canPay: order && 
              order.paymentStatus === 'UNPAID' && 
              (order.status === 'PENDING' || order.status === 'CONFIRMED')
    })
    
    return order && 
           order.paymentStatus === 'UNPAID' && 
           (order.status === 'PENDING' || order.status === 'CONFIRMED')
  },

  // 判断是否可以取消
  canCancel() {
    const order = this.data.orderInfo
    return order && 
           (order.status === 'PENDING' || order.status === 'CONFIRMED') &&
           order.paymentStatus === 'UNPAID'
  }
})