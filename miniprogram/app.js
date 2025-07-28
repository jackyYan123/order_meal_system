// app.js
App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://10.23.146.62:8080',
    tableId: null,
    tableName: null,
    cart: [],
    orderInfo: null
  },

  onLaunch() {
    // 小程序启动时执行
    console.log('小程序启动')

    // 检查登录状态
    this.checkLoginStatus()

    // 获取桌台信息
    this.getTableInfo()

    // 初始化购物车
    this.initCart()
  },

  onShow() {
    // 小程序显示时执行
    console.log('小程序显示')
  },

  onHide() {
    // 小程序隐藏时执行
    console.log('小程序隐藏')
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')

    if (token && userInfo) {
      this.globalData.token = token
      this.globalData.userInfo = userInfo
    }
  },

  // 获取桌台信息
  getTableInfo() {
    // 从扫码或其他方式获取桌台信息
    const tableId = wx.getStorageSync('tableId')
    const tableName = wx.getStorageSync('tableName')

    if (tableId) {
      this.globalData.tableId = tableId
      this.globalData.tableName = tableName
    }
  },

  // 初始化购物车
  initCart() {
    const cart = wx.getStorageSync('cart')
    if (cart && Array.isArray(cart)) {
      this.globalData.cart = cart
    } else {
      this.globalData.cart = []
      wx.setStorageSync('cart', [])
    }
  },

  // 添加商品到购物车
  addToCart(dish, quantity = 1) {
    const cart = [...this.globalData.cart]
    const existingIndex = cart.findIndex(item => item.dishId === dish.id)

    if (existingIndex >= 0) {
      // 商品已存在，增加数量
      cart[existingIndex].quantity += quantity
    } else {
      // 新商品，添加到购物车
      cart.push({
        dishId: dish.id,
        dishName: dish.name,
        price: dish.price,
        image: dish.image,
        quantity: quantity,
        specialRequests: ''
      })
    }

    this.globalData.cart = cart
    wx.setStorageSync('cart', cart)

    return cart
  },

  // 从购物车移除商品
  removeFromCart(dishId) {
    const cart = this.globalData.cart.filter(item => item.dishId !== dishId)
    this.globalData.cart = cart
    wx.setStorageSync('cart', cart)
    return cart
  },

  // 清空购物车
  clearCart() {
    this.globalData.cart = []
    wx.setStorageSync('cart', [])
  },

  // 用户登录
  login(callback) {
    wx.login({
      success: (res) => {
        if (res.code) {
          // 发送 res.code 到后台换取 openId, sessionKey, unionId
          this.getUserInfo((userInfo) => {
            // 调用后台登录接口
            this.requestLogin(res.code, userInfo, callback)
          })
        } else {
          console.log('登录失败！' + res.errMsg)
          callback && callback(false)
        }
      }
    })
  },

  // 获取用户信息
  getUserInfo(callback) {
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        this.globalData.userInfo = res.userInfo
        wx.setStorageSync('userInfo', res.userInfo)
        callback && callback(res.userInfo)
      },
      fail: () => {
        // 用户拒绝授权，使用默认信息
        const defaultUserInfo = {
          nickName: '顾客',
          avatarUrl: '/images/default-avatar.png'
        }
        this.globalData.userInfo = defaultUserInfo
        wx.setStorageSync('userInfo', defaultUserInfo)
        callback && callback(defaultUserInfo)
      }
    })
  },

  // 请求后台登录
  requestLogin(code, userInfo, callback) {
    wx.request({
      url: this.globalData.baseUrl + '/miniprogram/login',
      method: 'POST',
      data: {
        code: code,
        userInfo: userInfo
      },
      header: {
        'Content-Type': 'application/json'
      },
      success: (res) => {
        if (res.data && res.data.success) {
          const token = res.data.data.token
          this.globalData.token = token
          wx.setStorageSync('token', token)
          callback && callback(true)
        } else {
          console.log('登录失败：', res.data ? res.data.message : '未知错误')
          callback && callback(false)
        }
      },
      fail: (err) => {
        console.log('登录请求失败：', err)
        callback && callback(false)
      }
    })
  },

  // 退出登录
  logout() {
    this.globalData.token = null
    this.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')

    // 跳转到登录页
    wx.redirectTo({
      url: '/pages/login/login'
    })
  }
})