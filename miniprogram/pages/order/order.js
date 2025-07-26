// pages/order/order.js
const app = getApp()
const { getOrderList, cancelOrder } = require('../../api/order')
const { formatTime, formatPrice } = require('../../utils/util')

Page({
  data: {
    orders: [],
    loading: false,
    refreshing: false,
    hasMore: true,
    currentPage: 1,
    pageSize: 10,
    activeTab: 'all', // all, pending, confirmed, completed, cancelled
    tabs: [
      { key: 'all', name: '全部' },
      { key: 'pending', name: '待确认' },
      { key: 'confirmed', name: '制作中' },
      { key: 'completed', name: '已完成' },
      { key: 'cancelled', name: '已取消' }
    ]
  },

  onLoad() {
    this.loadOrders()
  },

  onShow() {
    // 页面显示时刷新订单列表
    this.refreshOrders()
  },

  onPullDownRefresh() {
    this.refreshOrders()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreOrders()
    }
  },

  // 切换标签
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    if (tab !== this.data.activeTab) {
      this.setData({
        activeTab: tab,
        orders: [],
        currentPage: 1,
        hasMore: true
      })
      this.loadOrders()
    }
  },

  // 加载订单列表
  async loadOrders() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    
    try {
      const params = {
        page: this.data.currentPage,
        size: this.data.pageSize
      }
      
      if (this.data.activeTab !== 'all') {
        params.status = this.data.activeTab.toUpperCase()
      }
      
      const result = await getOrderList(params)
      
      if (result.success) {
        // 处理MyBatis Plus分页数据结构
        const pageData = result.data
        const newOrders = pageData.records || []
        
        // 调试：打印订单数据结构
        console.log('订单列表数据：', newOrders)
        if (newOrders.length > 0) {
          console.log('第一个订单的totalAmount：', newOrders[0].totalAmount)
        }
        
        // 格式化订单数据，包括价格格式化和时间格式化
        const formattedOrders = newOrders.map(order => ({
          ...order,
          formattedTotalAmount: this.formatPrice(order.totalAmount),
          formattedCreatedAt: formatTime(order.createdAt),
          items: order.items ? order.items.map(item => ({
            ...item,
            formattedDishPrice: this.formatPrice(item.dishPrice)
          })) : []
        }))
        
        this.setData({
          orders: this.data.currentPage === 1 ? formattedOrders : [...this.data.orders, ...formattedOrders],
          hasMore: this.data.currentPage < pageData.pages,
          loading: false,
          refreshing: false
        })
      } else {
        throw new Error(result.message)
      }
    } catch (error) {
      console.error('加载订单失败:', error)
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
      this.setData({ loading: false, refreshing: false })
    }
    
    wx.stopPullDownRefresh()
  },

  // 刷新订单
  refreshOrders() {
    this.setData({
      refreshing: true,
      currentPage: 1,
      hasMore: true,
      orders: []
    })
    this.loadOrders()
  },

  // 加载更多订单
  loadMoreOrders() {
    this.setData({
      currentPage: this.data.currentPage + 1
    })
    this.loadOrders()
  },

  // 查看订单详情
  viewOrderDetail(e) {
    const orderNo = e.currentTarget.dataset.orderNo
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?orderNo=${orderNo}`
    })
  },

  // 取消订单
  async cancelOrder(e) {
    const orderId = e.currentTarget.dataset.orderId
    const order = this.data.orders.find(o => o.id === orderId)
    
    if (!order) return
    
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '取消中...' })
            
            const result = await cancelOrder(orderId, '用户主动取消')
            
            if (result.success) {
              wx.showToast({
                title: '订单已取消',
                icon: 'success'
              })
              
              // 更新订单状态
              const updatedOrders = this.data.orders.map(o => {
                if (o.id === orderId) {
                  return { ...o, status: 'CANCELLED' }
                }
                return o
              })
              
              this.setData({ orders: updatedOrders })
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

  // 再次购买
  buyAgain(e) {
    const orderId = e.currentTarget.dataset.orderId
    const order = this.data.orders.find(o => o.id === orderId)
    
    if (!order || !order.items) return
    
    // 清空购物车
    app.globalData.cart = []
    
    // 将订单商品添加到购物车
    order.items.forEach(item => {
      app.globalData.cart.push({
        dishId: item.dishId,
        dishName: item.dishName,
        price: item.dishPrice,
        image: item.dishImage,
        quantity: item.quantity,
        specialRequests: item.specialRequests || ''
      })
    })
    
    wx.showToast({
      title: '已添加到购物车',
      icon: 'success'
    })
    
    // 跳转到购物车页面
    setTimeout(() => {
      wx.switchTab({
        url: '/pages/cart/cart'
      })
    }, 1500)
  },

  // 联系客服
  contactService(e) {
    const orderId = e.currentTarget.dataset.orderId
    wx.makePhoneCall({
      phoneNumber: '400-123-4567'
    })
  },

  // 格式化时间
  formatTime,
  
  // 格式化价格
  formatPrice(price) {
    //console.log('格式化价格，原始值：', price, '类型：', typeof price)
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

  // 获取订单状态文本
  getStatusText(status) {
    const statusMap = {
      'PENDING': '待确认',
      'CONFIRMED': '制作中',
      'PREPARING': '制作中',
      'READY': '待取餐',
      'COMPLETED': '已完成',
      'CANCELLED': '已取消'
    }
    return statusMap[status] || status
  },

  // 获取订单状态样式
  getStatusClass(status) {
    const classMap = {
      'PENDING': 'status-pending',
      'CONFIRMED': 'status-confirmed',
      'PREPARING': 'status-confirmed',
      'READY': 'status-ready',
      'COMPLETED': 'status-completed',
      'CANCELLED': 'status-cancelled'
    }
    return classMap[status] || ''
  },

  // 判断是否可以取消订单
  canCancelOrder(status) {
    return ['PENDING', 'CONFIRMED'].includes(status)
  },

  // 跳转到菜单页面
  goToMenu() {
    wx.switchTab({
      url: '/pages/menu/menu'
    })
  }
})