// pages/menu/menu.js
const app = getApp()
const { getCategoryList, getDishList, searchDishes } = require('../../api/menu')
const { formatCurrency, calculateCartCount, debounce } = require('../../utils/util')

Page({
  data: {
    categories: [],
    dishes: [],
    currentCategoryId: null,
    searchKeyword: '',
    loading: false,
    cartCount: 0,
    showSearch: false,
    page: 1,
    hasMore: true,
    addToCartTimer: null
  },

  onLoad(options) {
    // 获取传入的分类ID
    if (options.categoryId) {
      this.setData({
        currentCategoryId: parseInt(options.categoryId)
      })
    }

    // 加载数据
    this.loadCategories()
    this.loadDishes()
    this.updateCartCount()
  },

  onShow() {
    // 更新购物车数量
    this.updateCartCount()
    
    // 检查是否有从首页传递的分类ID
    if (app.globalData.selectedCategoryId !== undefined) {
      this.setData({
        currentCategoryId: app.globalData.selectedCategoryId
      })
      // 清除全局数据中的分类ID，避免重复使用
      app.globalData.selectedCategoryId = undefined
      // 重新加载菜品数据
      this.loadDishes(true)
    }
  },

  // 加载分类列表
  async loadCategories() {
    try {
      const res = await getCategoryList()
      this.setData({
        categories: res.data || []
      })
    } catch (error) {
      console.error('加载分类失败：', error)
    }
  },

  // 加载菜品列表
  async loadDishes(reset = true) {
    if (this.data.loading) return

    try {
      this.setData({ loading: true })

      const params = {
        page: reset ? 1 : this.data.page,
        size: 10
      }

      if (this.data.currentCategoryId) {
        params.categoryId = this.data.currentCategoryId
      }

      if (this.data.searchKeyword) {
        params.keyword = this.data.searchKeyword
      }

      const res = await getDishList(params)
      console.log('菜品数据响应：', res)
      
      // 处理不同的数据结构
      let newDishes = []
      if (res && res.data) {
        if (Array.isArray(res.data)) {
          newDishes = res.data
        } else if (res.data.records && Array.isArray(res.data.records)) {
          newDishes = res.data.records
        } else if (res.data.list && Array.isArray(res.data.list)) {
          newDishes = res.data.list
        } else if (res.data.content && Array.isArray(res.data.content)) {
          newDishes = res.data.content
        }
      }

      // 处理图片路径、价格格式化和购物车数量
      const cart = app.globalData.cart || []
      newDishes = newDishes.map(dish => {
        const cartItem = cart.find(item => item.dishId === dish.id)
        return {
          ...dish,
          image: dish.image || dish.imageUrl || '/images/default-dish.png',
          formattedPrice: formatCurrency(dish.price),
          cartQuantity: cartItem ? cartItem.quantity : 0
        }
      })

      console.log('处理后的菜品数据：', newDishes)

      this.setData({
        dishes: reset ? newDishes : [...this.data.dishes, ...newDishes],
        page: reset ? 2 : this.data.page + 1,
        hasMore: newDishes.length === params.size,
        loading: false
      })

    } catch (error) {
      console.error('加载菜品失败：', error)
      this.setData({ loading: false })
      
      // 显示错误信息
      wx.showToast({
        title: '加载菜品失败',
        icon: 'none'
      })
    }
  },

  // 切换分类
  switchCategory(e) {
    const categoryId = e.currentTarget.dataset.categoryId
    this.setData({
      currentCategoryId: categoryId,
      searchKeyword: '',
      showSearch: false
    })
    this.loadDishes(true)
  },

  // 显示/隐藏搜索
  toggleSearch() {
    this.setData({
      showSearch: !this.data.showSearch,
      searchKeyword: '',
      currentCategoryId: null
    })
    
    if (!this.data.showSearch) {
      this.loadDishes(true)
    }
  },

  // 搜索输入
  onSearchInput: debounce(function(e) {
    const keyword = e.detail.value.trim()
    this.setData({
      searchKeyword: keyword,
      currentCategoryId: null
    })
    
    if (keyword) {
      this.searchDishes(keyword)
    } else {
      this.loadDishes(true)
    }
  }, 500),

  // 搜索菜品
  async searchDishes(keyword) {
    try {
      this.setData({ loading: true })
      const res = await searchDishes(keyword)
      console.log('搜索结果：', res)
      
      // 处理搜索结果数据结构
      let searchResults = []
      if (res && res.data) {
        if (Array.isArray(res.data)) {
          searchResults = res.data
        } else if (res.data.records && Array.isArray(res.data.records)) {
          searchResults = res.data.records
        } else if (res.data.list && Array.isArray(res.data.list)) {
          searchResults = res.data.list
        } else if (res.data.content && Array.isArray(res.data.content)) {
          searchResults = res.data.content
        }
      }

      // 处理图片路径、价格格式化和购物车数量
      const cart = app.globalData.cart || []
      searchResults = searchResults.map(dish => {
        const cartItem = cart.find(item => item.dishId === dish.id)
        return {
          ...dish,
          image: dish.image || dish.imageUrl || '/images/default-dish.png',
          formattedPrice: formatCurrency(dish.price),
          cartQuantity: cartItem ? cartItem.quantity : 0
        }
      })

      this.setData({
        dishes: searchResults,
        loading: false,
        hasMore: false
      })
    } catch (error) {
      console.error('搜索失败：', error)
      this.setData({ loading: false })
      
      wx.showToast({
        title: '搜索失败',
        icon: 'none'
      })
    }
  },

  // 添加到购物车
  addToCart(e) {
    const dish = e.currentTarget.dataset.dish
    
    // 检查桌台
    if (!app.globalData.tableId) {
      wx.showToast({
        title: '请先选择桌台',
        icon: 'none'
      })
      return
    }

    // 检查库存
    if (dish.stock <= 0) {
      wx.showToast({
        title: '该菜品已售罄',
        icon: 'none'
      })
      return
    }

    // 防抖处理
    if (this.data.addToCartTimer) {
      clearTimeout(this.data.addToCartTimer)
    }
    
    this.setData({
      addToCartTimer: setTimeout(() => {
        this.performAddToCart(dish)
      }, 100)
    })
  },

  // 执行添加到购物车操作
  performAddToCart(dish) {
    // 批量更新数据，减少setData调用
    const updates = {}
    
    // 更新购物车数据
    const cart = app.globalData.cart || []
    const existingItem = cart.find(item => item.dishId === dish.id)
    
    let newQuantity = 1
    if (existingItem) {
      existingItem.quantity += 1
      newQuantity = existingItem.quantity
    } else {
      cart.push({
        dishId: dish.id,
        dishName: dish.name,
        price: dish.price,
        image: dish.image,
        quantity: 1,
        specialRequests: ''
      })
    }
    
    // 更新特定菜品的数量显示，避免更新整个数组
    const dishIndex = this.data.dishes.findIndex(d => d.id === dish.id)
    if (dishIndex !== -1) {
      updates[`dishes[${dishIndex}].cartQuantity`] = newQuantity
    }
    
    // 更新购物车总数
    updates.cartCount = calculateCartCount(cart)
    
    // 一次性更新所有数据
    this.setData(updates)
    
    // 更新全局数据
    app.globalData.cart = cart
    wx.setStorageSync('cart', cart)

    // 显示成功提示
    wx.showToast({
      title: '已添加到购物车',
      icon: 'success',
      duration: 1000
    })

    // 触觉反馈
    wx.vibrateShort()
  },

  // 减少购物车商品
  removeFromCart(e) {
    const dish = e.currentTarget.dataset.dish
    const cart = app.globalData.cart || []
    const existingItem = cart.find(item => item.dishId === dish.id)

    if (existingItem) {
      // 批量更新数据，减少setData调用
      const updates = {}
      let newQuantity = 0

      if (existingItem.quantity > 1) {
        existingItem.quantity -= 1
        newQuantity = existingItem.quantity
      } else {
        const index = cart.findIndex(item => item.dishId === dish.id)
        cart.splice(index, 1)
        newQuantity = 0
      }

      // 更新特定菜品的数量显示，避免更新整个数组
      const dishIndex = this.data.dishes.findIndex(d => d.id === dish.id)
      if (dishIndex !== -1) {
        updates[`dishes[${dishIndex}].cartQuantity`] = newQuantity
      }

      // 更新购物车总数
      updates.cartCount = calculateCartCount(cart)

      // 一次性更新所有数据
      this.setData(updates)

      // 更新全局数据
      app.globalData.cart = cart
      wx.setStorageSync('cart', cart)

      wx.vibrateShort()
    }
  },

  // 获取菜品在购物车中的数量
  getDishQuantityInCart(dishId) {
    const cart = app.globalData.cart || []
    const item = cart.find(item => item.dishId === dishId)
    return item ? item.quantity : 0
  },

  // 更新购物车数量
  updateCartCount() {
    const cart = app.globalData.cart || []
    const count = calculateCartCount(cart)
    this.setData({ cartCount: count })
  },

  // 更新菜品的购物车数量显示
  updateDishCartQuantity() {
    const cart = app.globalData.cart || []
    const updatedDishes = this.data.dishes.map(dish => {
      const cartItem = cart.find(item => item.dishId === dish.id)
      return {
        ...dish,
        cartQuantity: cartItem ? cartItem.quantity : 0
      }
    })
    this.setData({ dishes: updatedDishes })
  },

  // 查看菜品详情
  viewDishDetail(e) {
    const dishId = e.currentTarget.dataset.dishId
    // 暂时显示菜品信息，后续可以创建菜品详情页
    wx.showToast({
      title: '菜品详情功能开发中',
      icon: 'none'
    })
  },

  // 跳转到购物车
  goToCart() {
    if (this.data.cartCount === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      })
      return
    }

    wx.switchTab({
      url: '/pages/cart/cart'
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadDishes(true).finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 上拉加载更多
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading && !this.data.searchKeyword) {
      this.loadDishes(false)
    }
  },

  // 图片加载错误处理
  onImageError(e) {
    console.log('图片加载失败：', e)
    // 可以设置默认图片或其他处理
  },

  // 格式化价格
  formatPrice(price) {
    return formatCurrency(price)
  }
})