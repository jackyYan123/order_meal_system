// pages/cart/cart.js
const app = getApp()
const { createOrder } = require('../../api/order')
const { formatCurrency, calculateCartTotal, calculateCartCount, showConfirm } = require('../../utils/util')

Page({
  data: {
    cartItems: [],
    totalPrice: 0,
    totalCount: 0,
    tableInfo: {
      id: null,
      name: ''
    },
    remarks: '',
    submitting: false,
    inputFocused: false,
    remarksInputTimer: null
  },

  onLoad() {
    this.loadCartData()
    this.loadTableInfo()
  },

  onShow() {
    this.loadCartData()
    this.loadTableInfo()
  },

  onReady() {
    // 页面初次渲染完成
    this.loadCartData()
    this.loadTableInfo()
  },

  // 加载购物车数据
  loadCartData() {
    const cart = app.globalData.cart || []
    const totalPrice = calculateCartTotal(cart)
    const totalCount = calculateCartCount(cart)

    // 格式化购物车商品的价格
    const formattedCartItems = cart.map(item => ({
      ...item,
      formattedPrice: formatCurrency(item.price),
      formattedSubtotal: formatCurrency(item.price * item.quantity)
    }))

    this.setData({
      cartItems: formattedCartItems,
      totalPrice,
      totalCount,
      formattedTotalPrice: formatCurrency(totalPrice)
    })
  },

  // 加载桌台信息
  loadTableInfo() {
    // 优先从全局数据获取
    let tableId = app.globalData.tableId;
    let tableName = app.globalData.tableName;
    
    // 如果全局数据为空，从本地存储获取
    if (!tableId) {
      tableId = wx.getStorageSync('tableId');
      tableName = wx.getStorageSync('tableName');
      
      // 同步到全局数据
      if (tableId) {
        app.globalData.tableId = tableId;
        app.globalData.tableName = tableName;
      }
    }
    
    // 更新页面数据
    this.setData({
      'tableInfo.id': tableId,
      'tableInfo.name': tableName || '未选择桌台'
    });
  },

  // 增加商品数量
  increaseQuantity(e) {
    const index = e.currentTarget.dataset.index
    const cartItems = [...this.data.cartItems]

    cartItems[index].quantity += 1

    this.updateCart(cartItems)
  },

  // 减少商品数量
  decreaseQuantity(e) {
    const index = e.currentTarget.dataset.index
    const cartItems = [...this.data.cartItems]

    if (cartItems[index].quantity > 1) {
      cartItems[index].quantity -= 1
      this.updateCart(cartItems)
    } else {
      this.removeItem(index)
    }
  },

  // 移除商品
  async removeItem(index) {
    const confirmed = await showConfirm('确定要移除这个商品吗？')
    if (!confirmed) return

    const cartItems = [...this.data.cartItems]
    cartItems.splice(index, 1)

    this.updateCart(cartItems)

    wx.showToast({
      title: '已移除',
      icon: 'success',
      duration: 1000
    })
  },

  // 更新购物车
  updateCart(cartItems) {
    const totalPrice = calculateCartTotal(cartItems)
    const totalCount = calculateCartCount(cartItems)

    // 格式化购物车商品的价格
    const formattedCartItems = cartItems.map(item => ({
      ...item,
      formattedPrice: formatCurrency(item.price),
      formattedSubtotal: formatCurrency(item.price * item.quantity)
    }))

    // 更新本地数据
    this.setData({
      cartItems: formattedCartItems,
      totalPrice,
      totalCount,
      formattedTotalPrice: formatCurrency(totalPrice)
    })

    // 更新全局数据
    app.globalData.cart = cartItems
    wx.setStorageSync('cart', cartItems)
  },

  // 清空购物车
  async clearCart() {
    if (this.data.cartItems.length === 0) return

    const confirmed = await showConfirm('确定要清空购物车吗？')
    if (!confirmed) return

    this.updateCart([])

    wx.showToast({
      title: '购物车已清空',
      icon: 'success'
    })
  },

  // 添加特殊要求
  addSpecialRequest(e) {
    const index = e.currentTarget.dataset.index

    wx.showModal({
      title: '特殊要求',
      content: this.data.cartItems[index].specialRequests || '',
      editable: true,
      placeholderText: '请输入特殊要求，如：不要辣、少盐等',
      success: (res) => {
        if (res.confirm) {
          const cartItems = [...this.data.cartItems]
          cartItems[index].specialRequests = res.content || ''
          this.updateCart(cartItems)
        }
      }
    })
  },

  // 备注输入
  onRemarksInput(e) {
    // 防抖处理输入
    if (this.data.remarksInputTimer) {
      clearTimeout(this.data.remarksInputTimer)
    }
    
    this.setData({
      remarksInputTimer: setTimeout(() => {
        this.setData({
          remarks: e.detail.value
        })
      }, 300)
    })
  },

  // 备注输入框获得焦点
  onRemarksFocus() {
    this.setData({
      inputFocused: true
    })
  },

  // 备注输入框失去焦点
  onRemarksBlur() {
    this.setData({
      inputFocused: false
    })
  },

  // 选择桌台
  selectTable() {
    wx.showToast({
      title: '请返回首页扫码选择桌台',
      icon: 'none'
    })
  },

  // 提交订单
  async submitOrder() {
    // 验证购物车
    if (this.data.cartItems.length === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      })
      return
    }

    // 验证桌台
    if (!this.data.tableInfo.id) {
      wx.showToast({
        title: '请先选择桌台',
        icon: 'none'
      })
      return
    }

    // 验证登录状态
    if (!app.globalData.token) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })

      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/login/login'
        })
      }, 1500)
      return
    }

    try {
      this.setData({ submitting: true })

      // 构建订单数据
      const orderData = {
        tableId: this.data.tableInfo.id,
        customerId: app.globalData.userInfo?.id,
        remarks: this.data.remarks,
        items: this.data.cartItems.map(item => ({
          dishId: item.dishId,
          quantity: item.quantity,
          specialRequests: item.specialRequests || ''
        }))
      }

      // 创建订单
      const res = await createOrder(orderData)

      if (res.success) {
        // 清空购物车
        this.updateCart([])

        // 保存订单信息
        app.globalData.orderInfo = res.data

        wx.showToast({
          title: '订单提交成功',
          icon: 'success'
        })

        // 跳转到订单详情页
        setTimeout(() => {
          wx.redirectTo({
            url: `/pages/order-detail/order-detail?orderNo=${res.data.orderNo}`
          })
        }, 1500)
      }

    } catch (error) {
      console.error('提交订单失败：', error)
      wx.showToast({
        title: error.message || '提交失败',
        icon: 'none'
      })
    } finally {
      this.setData({ submitting: false })
    }
  },

  // 继续购物
  continueShopping() {
    wx.switchTab({
      url: '/pages/menu/menu'
    })
  },

  // 格式化价格
  formatPrice(price) {
    return formatCurrency(price)
  },

  // 获取菜品在购物车中的数量（用于模板调用）
  getItemQuantity(item) {
    return item.quantity || 0
  }
})