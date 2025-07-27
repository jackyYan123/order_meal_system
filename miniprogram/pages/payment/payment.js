// pages/payment/payment.js
const app = getApp()
const { getOrderDetail, getOrderByOrderNo } = require('../../api/order')
const { wechatPay, alipay } = require('../../api/payment')
const { formatPrice } = require('../../utils/util')

Page({
  data: {
    orderId: null,
    orderNo: null,
    order: null,
    loading: true,
    paying: false,
    paymentMethods: [
      {
        id: 'WECHAT',
        name: '微信支付',
        icon: '💳',
        desc: '推荐使用微信支付'
      },
      {
        id: 'ALIPAY',
        name: '支付宝',
        icon: '💰',
        desc: '支持支付宝快捷支付'
      }
    ],
    selectedPaymentMethod: 'WECHAT'
  },

  onLoad(options) {
    console.log('支付页面参数：', options)
    
    if (options.orderId) {
      this.setData({ orderId: options.orderId })
      this.loadOrderDetail()
    } else if (options.orderNo) {
      this.setData({ orderNo: options.orderNo })
      this.loadOrderDetailByOrderNo()
    }
  },

  // 加载订单详情
  async loadOrderDetail() {
    if (!this.data.orderId) return
    
    try {
      this.setData({ loading: true })
      
      const result = await getOrderDetail(this.data.orderId)
      
      if (result.success) {
        const order = result.data
        
        // 检查订单状态
        if (order.paymentStatus === 'PAID') {
          wx.showToast({
            title: '订单已支付',
            icon: 'success'
          })
          
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
          return
        }
        
        if (order.status === 'CANCELLED') {
          wx.showToast({
            title: '订单已取消',
            icon: 'none'
          })
          
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
          return
        }
        
        // 格式化价格数据
        const formattedOrder = {
          ...order,
          formattedTotalAmount: formatPrice(order.totalAmount),
          formattedDishAmount: formatPrice(order.totalAmount || 0), // 使用totalAmount作为商品总计
          formattedServiceAmount: formatPrice(0), // 暂时设为0
          formattedDiscountAmount: formatPrice(0) // 暂时设为0
        }

        this.setData({
          order: formattedOrder,
          loading: false
        })
      } else {
        throw new Error(result.message)
      }
    } catch (error) {
      console.error('加载订单详情失败:', error)
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
      this.setData({ loading: false })
      
      setTimeout(() => {
        wx.navigateBack()
      }, 2000)
    }
  },

  // 根据订单号加载订单详情
  async loadOrderDetailByOrderNo() {
    if (!this.data.orderNo) return
    
    try {
      this.setData({ loading: true })
      
      console.log('查询订单号：', this.data.orderNo)
      const result = await getOrderByOrderNo(this.data.orderNo)
      
      if (result.success) {
        const order = result.data
        
        // 检查订单状态
        if (order.paymentStatus === 'PAID') {
          wx.showToast({
            title: '订单已支付',
            icon: 'success'
          })
          
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
          return
        }
        
        if (order.status === 'CANCELLED') {
          wx.showToast({
            title: '订单已取消',
            icon: 'none'
          })
          
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
          return
        }
        
        // 格式化价格数据
        const formattedOrder = {
          ...order,
          formattedTotalAmount: formatPrice(order.totalAmount),
          formattedDishAmount: formatPrice(order.totalAmount || 0), // 使用totalAmount作为商品总计
          formattedServiceAmount: formatPrice(0), // 暂时设为0
          formattedDiscountAmount: formatPrice(0) // 暂时设为0
        }

        this.setData({
          order: formattedOrder,
          loading: false
        })
      } else {
        throw new Error(result.message)
      }
    } catch (error) {
      console.error('根据订单号加载订单详情失败:', error)
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
      this.setData({ loading: false })
      
      setTimeout(() => {
        wx.navigateBack()
      }, 2000)
    }
  },

  // 选择支付方式
  selectPaymentMethod(e) {
    const method = e.currentTarget.dataset.method
    this.setData({
      selectedPaymentMethod: method
    })
  },

  // 确认支付
  async confirmPayment() {
    if (!this.data.order || this.data.paying) return
    
    if (!this.data.selectedPaymentMethod) {
      wx.showToast({
        title: '请选择支付方式',
        icon: 'none'
      })
      return
    }
    
    this.setData({ paying: true })
    
    try {
      wx.showLoading({ title: '支付中...' })
      
      const paymentData = {
        orderNo: this.data.order.orderNo,
        amount: this.data.order.totalAmount,
        paymentMethod: this.data.selectedPaymentMethod,
        description: `订单支付-${this.data.order.orderNo}`
      }
      
      let result
      
      if (this.data.selectedPaymentMethod === 'WECHAT') {
        // 微信支付
        result = await wechatPay(paymentData)
      } else if (this.data.selectedPaymentMethod === 'ALIPAY') {
        // 支付宝支付
        result = await alipay(paymentData)
      } else {
        throw new Error('暂不支持该支付方式')
      }
      
      if (result.success) {
        // 支付成功
        wx.showToast({
          title: '支付成功',
          icon: 'success'
        })
        
        // 跳转到支付结果页
        setTimeout(() => {
          const orderParam = this.data.orderNo ? `orderNo=${this.data.orderNo}` : `orderId=${this.data.order.id}`
          wx.redirectTo({
            url: `/pages/payment-result/payment-result?${orderParam}&status=success`
          })
        }, 1500)
      } else {
        throw new Error(result.message || '支付失败')
      }
    } catch (error) {
      console.error('支付失败:', error)
      
      let errorMessage = '支付失败'
      if (error.message) {
        if (error.message.includes('cancel')) {
          errorMessage = '支付已取消'
        } else {
          errorMessage = error.message
        }
      }
      
      wx.showToast({
        title: errorMessage,
        icon: 'none'
      })
      
      // 如果是取消支付，不跳转
      if (!error.message || !error.message.includes('cancel')) {
        setTimeout(() => {
          const orderParam = this.data.orderNo ? `orderNo=${this.data.orderNo}` : `orderId=${this.data.order.id}`
          wx.redirectTo({
            url: `/pages/payment-result/payment-result?${orderParam}&status=failed&message=${encodeURIComponent(errorMessage)}`
          })
        }, 2000)
      }
    } finally {
      wx.hideLoading()
      this.setData({ paying: false })
    }
  },

  // 查看订单详情
  viewOrderDetail() {
    if (!this.data.order) return
    
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?orderId=${this.data.order.id}`
    })
  },

  // 格式化价格
  formatPrice
})