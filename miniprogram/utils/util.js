// utils/util.js - 通用工具函数

/**
 * 格式化时间
 * @param {Date|string} date - 日期对象或日期字符串
 * @param {string} format - 格式化字符串，默认 'YYYY-MM-DD HH:mm:ss'
 */
function formatTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  
  let dateObj
  
  if (typeof date === 'string') {
    // 处理后端返回的标准格式: "2025-07-27 18:21:08"
    // 也兼容ISO格式: "2025-07-27T18:21:08"
    const dateStr = date.replace('T', ' ').replace(/\.\d{3}Z?$/, '')
    dateObj = new Date(dateStr)
    
    // 如果解析失败，尝试直接解析原字符串
    if (isNaN(dateObj.getTime())) {
      dateObj = new Date(date)
    }
  } else if (date instanceof Date) {
    dateObj = date
  } else {
    console.warn('formatTime: 无效的日期参数类型', typeof date, date)
    return ''
  }
  
  // 检查日期对象是否有效
  if (!(dateObj instanceof Date) || isNaN(dateObj.getTime())) {
    console.warn('formatTime: 无效的日期参数', date)
    return ''
  }
  
  const year = dateObj.getFullYear()
  const month = dateObj.getMonth() + 1
  const day = dateObj.getDate()
  const hour = dateObj.getHours()
  const minute = dateObj.getMinutes()
  const second = dateObj.getSeconds()

  const formatNumber = (n) => {
    n = n.toString()
    return n[1] ? n : '0' + n
  }

  return format
    .replace('YYYY', year)
    .replace('MM', formatNumber(month))
    .replace('DD', formatNumber(day))
    .replace('HH', formatNumber(hour))
    .replace('mm', formatNumber(minute))
    .replace('ss', formatNumber(second))
}

/**
 * 格式化价格
 * @param {number} price - 价格
 * @param {string} symbol - 货币符号，默认 '¥'
 */
function formatPrice(price, symbol = '¥') {
  if (price === null || price === undefined) return symbol + '0.00'
  return symbol + parseFloat(price).toFixed(2)
}

/**
 * 格式化货币
 * @param {number} amount - 金额
 */
function formatCurrency(amount) {
  return formatPrice(amount)
}

/**
 * 格式化订单时间显示
 * @param {string} timeStr - 时间字符串
 * @param {string} label - 时间标签
 */
function formatOrderTime(timeStr, label) {
  if (!timeStr) {
    return `${label}: --`
  }
  
  const time = new Date(timeStr.replace('T', ' ').replace(/\.\d{3}Z?$/, ''))
  if (isNaN(time.getTime())) {
    return `${label}: --`
  }
  
  const now = new Date()
  
  // 如果是今天，显示时分
  if (time.toDateString() === now.toDateString()) {
    return `${label}: ${time.getHours().toString().padStart(2, '0')}:${time.getMinutes().toString().padStart(2, '0')}`
  }
  
  // 如果不是今天，显示月日时分
  return `${label}: ${(time.getMonth() + 1).toString().padStart(2, '0')}-${time.getDate().toString().padStart(2, '0')} ${time.getHours().toString().padStart(2, '0')}:${time.getMinutes().toString().padStart(2, '0')}`
}

/**
 * 格式化预计完成时间
 * @param {string} estimatedTime - 预计完成时间
 * @param {string} orderStatus - 订单状态
 */
function formatEstimatedTime(estimatedTime, orderStatus) {
  if (!estimatedTime) return '预计完成: --'
  
  const estimated = new Date(estimatedTime.replace('T', ' ').replace(/\.\d{3}Z?$/, ''))
  if (isNaN(estimated.getTime())) return '预计完成: --'
  
  const now = new Date()
  
  if (orderStatus === 'COMPLETED') {
    return '已完成'
  }
  
  if (orderStatus === 'CANCELLED') {
    return '已取消'
  }
  
  if (estimated > now) {
    const diffMinutes = Math.ceil((estimated - now) / (1000 * 60))
    if (diffMinutes <= 0) {
      return '预计完成: 即将完成'
    } else if (diffMinutes < 60) {
      return `预计完成: 还需${diffMinutes}分钟`
    } else {
      const hours = Math.floor(diffMinutes / 60)
      const minutes = diffMinutes % 60
      if (minutes === 0) {
        return `预计完成: 还需${hours}小时`
      } else {
        return `预计完成: 还需${hours}小时${minutes}分钟`
      }
    }
  } else {
    return '预计完成: 即将完成'
  }
}

/**
 * 计算购物车总数量
 * @param {Array} cart - 购物车数组
 */
function calculateCartCount(cart) {
  if (!cart || !Array.isArray(cart)) return 0
  return cart.reduce((total, item) => total + (item.quantity || 0), 0)
}

/**
 * 计算购物车总价
 * @param {Array} cart - 购物车数组
 */
function calculateCartTotal(cart) {
  if (!cart || !Array.isArray(cart)) return 0
  return cart.reduce((total, item) => {
    const price = parseFloat(item.price || 0)
    const quantity = parseInt(item.quantity || 0)
    return total + (price * quantity)
  }, 0)
}

/**
 * 防抖函数
 * @param {Function} func - 要防抖的函数
 * @param {number} delay - 延迟时间（毫秒）
 */
function debounce(func, delay = 300) {
  let timeoutId
  return function (...args) {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => func.apply(this, args), delay)
  }
}

/**
 * 节流函数
 * @param {Function} func - 要节流的函数
 * @param {number} delay - 延迟时间（毫秒）
 */
function throttle(func, delay = 300) {
  let lastTime = 0
  return function (...args) {
    const now = Date.now()
    if (now - lastTime >= delay) {
      lastTime = now
      func.apply(this, args)
    }
  }
}

/**
 * 显示确认对话框
 * @param {string} content - 对话框内容
 * @param {string} title - 对话框标题
 */
function showConfirm(content, title = '提示') {
  return new Promise((resolve) => {
    wx.showModal({
      title,
      content,
      success: (res) => {
        resolve(res.confirm)
      },
      fail: () => {
        resolve(false)
      }
    })
  })
}

/**
 * 显示加载提示
 * @param {string} title - 提示文字
 */
function showLoading(title = '加载中...') {
  wx.showLoading({
    title,
    mask: true
  })
}

/**
 * 隐藏加载提示
 */
function hideLoading() {
  wx.hideLoading()
}

/**
 * 显示成功提示
 * @param {string} title - 提示文字
 */
function showSuccess(title) {
  wx.showToast({
    title,
    icon: 'success',
    duration: 2000
  })
}

/**
 * 显示错误提示
 * @param {string} title - 提示文字
 */
function showError(title) {
  wx.showToast({
    title,
    icon: 'none',
    duration: 2000
  })
}

/**
 * 获取当前页面路径
 */
function getCurrentPagePath() {
  try {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    return currentPage ? currentPage.route : ''
  } catch (e) {
    return ''
  }
}

/**
 * 深拷贝对象
 * @param {any} obj - 要拷贝的对象
 */
function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj)
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (typeof obj === 'object') {
    const clonedObj = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

/**
 * 生成随机字符串
 * @param {number} length - 字符串长度
 */
function generateRandomString(length = 8) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

module.exports = {
  formatTime,
  formatPrice,
  formatCurrency,
  formatOrderTime,
  formatEstimatedTime,
  calculateCartCount,
  calculateCartTotal,
  debounce,
  throttle,
  showConfirm,
  showLoading,
  hideLoading,
  showSuccess,
  showError,
  getCurrentPagePath,
  deepClone,
  generateRandomString
}