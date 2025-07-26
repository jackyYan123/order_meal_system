// pages/profile/profile.js
const app = getApp()

Page({
  data: {
    userInfo: null,
    isLogin: false,
    menuItems: [
      {
        icon: '📋',
        title: '我的订单',
        desc: '查看订单状态',
        url: '/pages/order/order'
      },
      {
        icon: '🎫',
        title: '优惠券',
        desc: '查看可用优惠',
        action: 'showComingSoon'
      },
      {
        icon: '📍',
        title: '收货地址',
        desc: '管理收货地址',
        action: 'showComingSoon'
      },
      {
        icon: '💬',
        title: '客服中心',
        desc: '在线客服服务',
        action: 'contactService'
      },
      {
        icon: 'ℹ️',
        title: '关于我们',
        desc: '了解更多信息',
        action: 'showAbout'
      }
    ]
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.checkLoginStatus()
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = app.globalData.token || wx.getStorageSync('token')
    const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
    
    this.setData({
      isLogin: !!token,
      userInfo: userInfo
    })
  },

  // 登录
  login() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除登录信息
          app.globalData.token = null
          app.globalData.userInfo = null
          app.globalData.cart = []
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          
          this.setData({
            isLogin: false,
            userInfo: null
          })
          
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          })
        }
      }
    })
  },

  // 菜单项点击
  onMenuItemTap(e) {
    const item = e.currentTarget.dataset.item
    
    if (!this.data.isLogin && item.url !== '/pages/about/about') {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      
      setTimeout(() => {
        this.login()
      }, 1500)
      return
    }
    
    if (item.action) {
      this[item.action]()
    } else if (item.url) {
      if (item.url.startsWith('/pages/order/order') || 
          item.url.startsWith('/pages/cart/cart') ||
          item.url.startsWith('/pages/menu/menu') ||
          item.url.startsWith('/pages/index/index')) {
        wx.switchTab({ url: item.url })
      } else {
        wx.navigateTo({ url: item.url })
      }
    }
  },

  // 联系客服
  contactService() {
    wx.showActionSheet({
      itemList: ['拨打客服电话', '在线客服'],
      success: (res) => {
        if (res.tapIndex === 0) {
          wx.makePhoneCall({
            phoneNumber: '400-123-4567'
          })
        } else if (res.tapIndex === 1) {
          wx.showToast({
            title: '在线客服功能开发中',
            icon: 'none'
          })
        }
      }
    })
  },

  // 显示即将推出
  showComingSoon() {
    wx.showToast({
      title: '功能开发中，敬请期待',
      icon: 'none'
    })
  },

  // 显示关于信息
  showAbout() {
    wx.showModal({
      title: '关于我们',
      content: '智慧点餐系统 v1.0\n\n为您提供便捷的点餐服务体验',
      showCancel: false,
      confirmText: '知道了'
    })
  },

  // 编辑用户信息
  editProfile() {
    wx.showToast({
      title: '编辑功能开发中',
      icon: 'none'
    })
  },

  // 设置
  openSettings() {
    wx.showToast({
      title: '设置功能开发中',
      icon: 'none'
    })
  }
})