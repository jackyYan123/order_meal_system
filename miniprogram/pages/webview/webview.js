// pages/webview/webview.js
Page({
  data: {
    url: ''
  },

  onLoad(options) {
    if (options.url) {
      this.setData({
        url: decodeURIComponent(options.url)
      })
    }
  },

  // 监听webview消息
  onMessage(e) {
    console.log('收到webview消息:', e.detail.data)
    
    const data = e.detail.data[0]
    if (data && data.type === 'payment_result') {
      // 处理支付结果
      if (data.success) {
        wx.showToast({
          title: '支付成功',
          icon: 'success'
        })
        
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        wx.showToast({
          title: data.message || '支付失败',
          icon: 'none'
        })
        
        setTimeout(() => {
          wx.navigateBack()
        }, 2000)
      }
    }
  }
})