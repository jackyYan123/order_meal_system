// utils/request.js - API请求封装
const app = getApp()

/**
 * 封装wx.request
 */
function request(options) {
  return new Promise((resolve, reject) => {
    // 显示加载提示
    if (options.loading !== false) {
      wx.showLoading({
        title: options.loadingText || '加载中...',
        mask: true
      })
    }

    wx.request({
      url: app.globalData.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': app.globalData.token ? `Bearer ${app.globalData.token}` : '',
        ...options.header
      },
      success: (res) => {
        // 隐藏加载提示
        if (options.loading !== false) {
          wx.hideLoading()
        }

        if (res.statusCode === 200) {
          if (res.data.success) {
            resolve(res.data)
          } else {
            // 业务错误
            handleBusinessError(res.data, reject)
          }
        } else if (res.statusCode === 401) {
          // 未授权，需要重新登录
          handleUnauthorized()
          reject(new Error('未授权'))
        } else {
          // HTTP错误
          const error = new Error(`HTTP ${res.statusCode}: ${res.data.message || '请求失败'}`)
          handleError(error, options.showError)
          reject(error)
        }
      },
      fail: (err) => {
        // 隐藏加载提示
        if (options.loading !== false) {
          wx.hideLoading()
        }

        // 网络错误
        const error = new Error(err.errMsg || '网络请求失败')
        handleError(error, options.showError)
        reject(error)
      }
    })
  })
}

/**
 * 处理业务错误
 */
function handleBusinessError(data, reject) {
  const error = new Error(data.message || '业务处理失败')
  error.code = data.code
  
  // 显示错误提示
  wx.showToast({
    title: data.message || '操作失败',
    icon: 'none',
    duration: 2000
  })
  
  reject(error)
}

/**
 * 处理未授权错误
 */
function handleUnauthorized() {
  // 清除本地存储的token
  app.globalData.token = null
  wx.removeStorageSync('token')
  
  // 提示用户重新登录
  wx.showModal({
    title: '提示',
    content: '登录已过期，请重新登录',
    showCancel: false,
    success: () => {
      // 跳转到登录页
      wx.redirectTo({
        url: '/pages/login/login'
      })
    }
  })
}

/**
 * 处理其他错误
 */
function handleError(error, showError = true) {
  console.error('请求错误：', error)
  
  if (showError !== false) {
    wx.showToast({
      title: error.message || '请求失败',
      icon: 'none',
      duration: 2000
    })
  }
}

/**
 * GET请求
 */
function get(url, data = {}, options = {}) {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  })
}

/**
 * POST请求
 */
function post(url, data = {}, options = {}) {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

/**
 * PUT请求
 */
function put(url, data = {}, options = {}) {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

/**
 * DELETE请求
 */
function del(url, data = {}, options = {}) {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  })
}

/**
 * 上传文件
 */
function uploadFile(filePath, url, formData = {}, options = {}) {
  return new Promise((resolve, reject) => {
    // 显示上传进度
    if (options.loading !== false) {
      wx.showLoading({
        title: '上传中...',
        mask: true
      })
    }

    wx.uploadFile({
      url: app.globalData.baseUrl + url,
      filePath,
      name: 'file',
      formData,
      header: {
        'Authorization': app.globalData.token ? `Bearer ${app.globalData.token}` : ''
      },
      success: (res) => {
        if (options.loading !== false) {
          wx.hideLoading()
        }

        try {
          const data = JSON.parse(res.data)
          if (data.success) {
            resolve(data)
          } else {
            handleBusinessError(data, reject)
          }
        } catch (e) {
          const error = new Error('响应数据解析失败')
          handleError(error)
          reject(error)
        }
      },
      fail: (err) => {
        if (options.loading !== false) {
          wx.hideLoading()
        }

        const error = new Error(err.errMsg || '上传失败')
        handleError(error)
        reject(error)
      }
    })
  })
}

module.exports = {
  request,
  get,
  post,
  put,
  del,
  uploadFile
}