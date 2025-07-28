// pages/index/index.js
const app = getApp()
const { getCategoryList, getRecommendedDishes, getPopularDishes } = require('../../api/menu')
const { formatCurrency, calculateCartCount } = require('../../utils/util')

Page({
  data: {
    tableInfo: {
      id: null,
      name: '请扫码选择桌台'
    },
    categories: [],
    recommendedDishes: [],
    popularDishes: [],
    loading: true,
    userInfo: null,
    cartCount: 0
  },

  onLoad(options) {
    // 获取桌台信息
    this.getTableInfo(options)
    
    // 获取用户信息
    this.getUserInfo()
    
    // 加载页面数据
    this.loadPageData()
  },

  onShow() {
    // 更新桌台信息
    this.updateTableInfo()
    
    // 更新用户信息
    this.getUserInfo()
    
    // 更新购物车数量
    this.updateCartCount()
  },

  // 获取桌台信息
  getTableInfo(options) {
    let tableId = options.tableId || app.globalData.tableId
    let tableName = options.tableName || app.globalData.tableName

    // 如果URL参数中有桌台信息，更新全局数据
    if (options.tableId) {
      app.globalData.tableId = options.tableId
      app.globalData.tableName = options.tableName || `桌台${options.tableId}`
      wx.setStorageSync('tableId', options.tableId)
      wx.setStorageSync('tableName', options.tableName || `桌台${options.tableId}`)
    }

    this.setData({
      'tableInfo.id': tableId,
      'tableInfo.name': tableName || '请扫码选择桌台'
    })
  },

  // 更新桌台信息
  updateTableInfo() {
    const tableId = app.globalData.tableId
    const tableName = app.globalData.tableName

    this.setData({
      'tableInfo.id': tableId,
      'tableInfo.name': tableName || '请扫码选择桌台'
    })
  },

  // 获取用户信息
  getUserInfo() {
    const userInfo = app.globalData.userInfo
    this.setData({
      userInfo
    })
  },

  // 加载页面数据
  async loadPageData() {
    try {
      this.setData({ loading: true })

      // 并行加载数据
      const [categoriesRes, recommendedRes, popularRes] = await Promise.all([
        getCategoryList(),
        getRecommendedDishes(),
        getPopularDishes()
      ])

      // 格式化菜品价格
      const formattedRecommendedDishes = (recommendedRes.data || []).map(dish => ({
        ...dish,
        formattedPrice: formatCurrency(dish.price)
      }))

      const formattedPopularDishes = (popularRes.data || []).map(dish => ({
        ...dish,
        formattedPrice: formatCurrency(dish.price)
      }))

      this.setData({
        categories: categoriesRes.data || [],
        recommendedDishes: formattedRecommendedDishes,
        popularDishes: formattedPopularDishes,
        loading: false
      })

    } catch (error) {
      console.error('加载页面数据失败：', error)
      this.setData({ loading: false })
    }
  },

  // 扫码选择桌台
  scanTable() {
    wx.scanCode({
      success: (res) => {
        try {
          // 解析二维码内容
          const qrData = JSON.parse(res.result)
          if (qrData.tableId) {
            // 检查是否更换了桌台
            const currentTableId = app.globalData.tableId
            const newTableId = qrData.tableId
            
            // 如果更换了桌台，提示用户是否清空购物车
            if (currentTableId && currentTableId !== newTableId && app.globalData.cart && app.globalData.cart.length > 0) {
              wx.showModal({
                title: '更换桌台',
                content: '更换桌台将清空当前购物车，是否继续？',
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    this.changeTable(qrData)
                  }
                }
              })
            } else {
              this.changeTable(qrData)
            }
          } else {
            wx.showToast({
              title: '无效的桌台二维码',
              icon: 'none'
            })
          }
        } catch (e) {
          wx.showToast({
            title: '二维码格式错误',
            icon: 'none'
          })
        }
      },
      fail: () => {
        wx.showToast({
          title: '扫码失败',
          icon: 'none'
        })
      }
    })
  },

  // 更换桌台
  changeTable(qrData) {
    // 清空购物车
    app.globalData.cart = []
    wx.setStorageSync('cart', [])
    
    // 更新桌台信息
    app.globalData.tableId = qrData.tableId
    app.globalData.tableName = qrData.tableName || `桌台${qrData.tableId}`
    
    wx.setStorageSync('tableId', qrData.tableId)
    wx.setStorageSync('tableName', qrData.tableName)

    this.setData({
      'tableInfo.id': qrData.tableId,
      'tableInfo.name': qrData.tableName || `桌台${qrData.tableId}`
    })

    // 更新购物车数量显示
    this.updateCartCount()

    wx.showToast({
      title: '桌台选择成功',
      icon: 'success'
    })
  },

  // 跳转到菜单页面
  goToMenu() {
    if (!this.data.tableInfo.id) {
      wx.showToast({
        title: '请先选择桌台',
        icon: 'none'
      })
      return
    }

    // 清除之前选择的分类，确保跳转到"全部"分类
    app.globalData.selectedCategoryId = null
    wx.switchTab({
      url: '/pages/menu/menu'
    })
  },

  // 跳转到分类菜单
  goToCategoryMenu(e) {
    const categoryId = e.currentTarget.dataset.categoryId
    if (!this.data.tableInfo.id) {
      wx.showToast({
        title: '请先选择桌台',
        icon: 'none'
      })
      return
    }

    // 先切换到菜单tab，然后通过全局数据传递分类ID
    app.globalData.selectedCategoryId = categoryId
    wx.switchTab({
      url: '/pages/menu/menu'
    })
  },

  // 跳转到购物车
  goToCart() {
    if (!this.data.tableInfo.id) {
      wx.showToast({
        title: '请先选择桌台',
        icon: 'none'
      })
      return
    }

    wx.switchTab({
      url: '/pages/cart/cart'
    })
  },

  // 跳转到订单页面
  goToOrder() {
    if (!this.data.tableInfo.id) {
      wx.showToast({
        title: '请先选择桌台',
        icon: 'none'
      })
      return
    }

    wx.switchTab({
      url: '/pages/order/order'
    })
  },

  // 跳转到个人中心
  goToProfile() {
    wx.switchTab({
      url: '/pages/profile/profile'
    })
  },

  // 查看菜品详情
  viewDishDetail(e) {
    const dishId = e.currentTarget.dataset.dishId
    // 暂时跳转到菜单页面，后续可以创建菜品详情页
    wx.switchTab({
      url: '/pages/menu/menu'
    })
  },

  // 添加到购物车
  addToCart(e) {
    const dish = e.currentTarget.dataset.dish
    
    // 检查菜品数据是否存在
    if (!dish) {
      console.error('菜品数据不存在:', e.currentTarget.dataset)
      wx.showToast({
        title: '菜品信息错误',
        icon: 'none'
      })
      return
    }

    // 检查菜品ID是否存在
    if (!dish.id) {
      console.error('菜品ID不存在:', dish)
      wx.showToast({
        title: '菜品ID错误',
        icon: 'none'
      })
      return
    }
    
    if (!this.data.tableInfo.id) {
      wx.showToast({
        title: '请先选择桌台',
        icon: 'none'
      })
      return
    }

    // 添加到购物车
    const cart = app.globalData.cart || []
    const existingItem = cart.find(item => item.dishId === dish.id)

    if (existingItem) {
      existingItem.quantity += 1
    } else {
      cart.push({
        dishId: dish.id,
        dishName: dish.name,
        price: dish.price,
        image: dish.image || dish.imageUrl || '/images/default-dish.png',
        quantity: 1,
        specialRequests: ''
      })
    }

    app.globalData.cart = cart
    wx.setStorageSync('cart', cart)

    wx.showToast({
      title: '已添加到购物车',
      icon: 'success'
    })

    // 更新购物车数量
    this.updateCartCount()
  },

  // 更新购物车数量
  updateCartCount() {
    const cart = app.globalData.cart || []
    const count = calculateCartCount(cart)
    this.setData({ cartCount: count })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadPageData().finally(() => {
      wx.stopPullDownRefresh()
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