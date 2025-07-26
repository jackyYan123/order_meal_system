// pages/login/login.js
const app = getApp()
const { post } = require('../../utils/request')

Page({
  data: {
    userInfo: null,
    hasUserInfo: false,
    canIUseGetUserProfile: false,
    loading: false
  },

  onLoad() {
    if (wx.getUserProfile) {
      this.setData({
        canIUseGetUserProfile: true
      })
    }

    // 检查是否已经登录
    this.checkLoginStatus()
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')

    if (token && userInfo) {
      // 验证token是否有效
      this.validateToken(token)
    }
  },

  // 验证token有效性
  async validateToken(token) {
    try {
      const res = await post('/miniprogram/validate', {}, {
        header: {
          'Authorization': `Bearer ${token}`
        },
        loading: false,
        showError: false
      })

      if (res.success && res.data) {
        // Token有效，跳转到首页
        wx.switchTab({
          url: '/pages/index/index'
        })
      } else {
        // Token无效，清除本地存储
        this.clearLoginInfo()
      }
    } catch (error) {
      // 验证失败，清除本地存储
      this.clearLoginInfo()
    }
  },

  // 清除登录信息
  clearLoginInfo() {
    app.globalData.token = null
    app.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
  },

  // 获取用户信息并登录
  getUserProfile() {
    if (this.data.loading) return

    this.setData({ loading: true })

    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        console.log('获取用户信息成功：', res.userInfo)
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })

        // 执行登录
        this.doWechatLogin(res.userInfo)
      },
      fail: (err) => {
        console.log('获取用户信息失败：', err)
        this.setData({ loading: false })
        wx.showToast({
          title: '需要授权才能使用完整功能',
          icon: 'none',
          duration: 2000
        })
      }
    })
  },

  // 执行微信登录
  doWechatLogin(userInfo) {
    wx.login({
      success: async (res) => {
        if (res.code) {
          try {
            console.log('微信登录code：', res.code)
            console.log('用户信息：', userInfo)

            // 调用后台登录接口
            const response = await post('/miniprogram/login', {
              code: res.code,
              userInfo: userInfo
            }, {
              loadingText: '登录中...'
            })

            this.handleLoginSuccess(response, userInfo)

          } catch (error) {
            console.error('登录请求失败：', error)
            this.setData({ loading: false })
            wx.showToast({
              title: error.message || '登录失败，请重试',
              icon: 'none'
            })
          }
        } else {
          this.setData({ loading: false })
          wx.showToast({
            title: '获取微信登录凭证失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.error('微信登录失败：', err)
        this.setData({ loading: false })
        wx.showToast({
          title: '微信登录失败',
          icon: 'none'
        })
      }
    })
  },

  // 游客模式登录
  guestLogin() {
    if (this.data.loading) return

    this.setData({ loading: true })

    const guestUserInfo = {
      nickName: '游客',
      avatarUrl: '/images/guest-icon.png'
    }

    wx.login({
      success: async (res) => {
        if (res.code) {
          try {
            console.log('游客登录code：', res.code)

            // 调用后台游客登录接口
            const response = await post('/miniprogram/guest-login', {
              code: res.code
            }, {
              loadingText: '登录中...'
            })

            this.handleLoginSuccess(response, guestUserInfo)

          } catch (error) {
            console.error('游客登录失败：', error)
            this.setData({ loading: false })
            wx.showToast({
              title: error.message || '登录失败，请重试',
              icon: 'none'
            })
          }
        } else {
          this.setData({ loading: false })
          wx.showToast({
            title: '获取登录凭证失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.error('游客登录失败：', err)
        this.setData({ loading: false })
        wx.showToast({
          title: '登录失败',
          icon: 'none'
        })
      }
    })
  },

  // 处理登录成功
  handleLoginSuccess(response, userInfo) {
    this.setData({ loading: false })

    if (response.success && response.data) {
      const { token, userType, userId, username, displayName, realName } = response.data

      console.log('登录成功：', response.data)

      // 构建最终的用户信息
      const finalUserInfo = {
        ...userInfo,
        userId: userId,
        username: username,
        displayName: displayName,
        realName: realName,
        userType: userType
      }

      // 对于游客登录，确保昵称和头像正确
      if (userInfo && userInfo.nickName === '游客') {
        finalUserInfo.nickName = '游客'
        // 使用游客专用头像
        finalUserInfo.avatarUrl = '/images/guest-icon.png'
      }

      // 保存登录信息到全局和本地存储
      app.globalData.token = token
      app.globalData.userInfo = finalUserInfo

      wx.setStorageSync('token', token)
      wx.setStorageSync('userInfo', finalUserInfo)

      wx.showToast({
        title: '登录成功',
        icon: 'success',
        duration: 1500
      })

      // 延迟跳转到首页
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/index/index'
        })
      }, 1500)

    } else {
      wx.showToast({
        title: response.message || '登录失败',
        icon: 'none'
      })
    }
  },

  // 查看用户协议
  viewUserAgreement() {
    wx.showModal({
      title: '用户协议',
      content: '这里是用户协议的内容...',
      showCancel: false,
      confirmText: '我知道了'
    })
  },

  // 查看隐私政策
  viewPrivacyPolicy() {
    wx.showModal({
      title: '隐私政策',
      content: '这里是隐私政策的内容...',
      showCancel: false,
      confirmText: '我知道了'
    })
  }
})