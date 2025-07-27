// api/payment.js - 支付相关API
const { get, post } = require('../utils/request')

/**
 * 创建支付
 * @param {Object} paymentData - 支付数据
 */
function createPayment(paymentData) {
  return post('/api/payments', paymentData)
}

/**
 * 处理支付
 * @param {number} paymentId - 支付ID
 */
function processPayment(paymentId) {
  return post(`/api/payments/${paymentId}/process`)
}

/**
 * 获取支付详情
 * @param {number} paymentId - 支付ID
 */
function getPaymentDetail(paymentId) {
  return get(`/api/payments/${paymentId}`)
}

/**
 * 根据订单ID获取支付记录
 * @param {number} orderId - 订单ID
 */
function getPaymentsByOrder(orderId) {
  return get(`/api/payments/order/${orderId}`)
}

/**
 * 微信支付
 * @param {Object} paymentData - 支付数据
 */
function wechatPay(paymentData) {
  return new Promise((resolve, reject) => {
    // 先创建支付记录
    createPayment(paymentData).then(payment => {
      // 处理支付
      return processPayment(payment.data.id)
    }).then(paymentResult => {
      // 调用微信支付
      if (paymentResult.data.paymentMethod === 'WECHAT') {
        wx.requestPayment({
          timeStamp: paymentResult.data.timeStamp || '',
          nonceStr: paymentResult.data.nonceStr || '',
          package: paymentResult.data.package || '',
          signType: paymentResult.data.signType || 'MD5',
          paySign: paymentResult.data.paySign || '',
          success: (res) => {
            resolve({
              success: true,
              data: res
            })
          },
          fail: (err) => {
            reject({
              success: false,
              message: err.errMsg || '支付失败'
            })
          }
        })
      } else {
        resolve(paymentResult)
      }
    }).catch(err => {
      reject(err)
    })
  })
}

/**
 * 支付宝支付
 * @param {Object} paymentData - 支付数据
 */
function alipay(paymentData) {
  return new Promise((resolve, reject) => {
    // 先创建支付记录
    createPayment(paymentData).then(payment => {
      // 处理支付
      return processPayment(payment.data.id)
    }).then(paymentResult => {
      // 调用支付宝支付
      if (paymentResult.data.paymentMethod === 'ALIPAY') {
        // 小程序中支付宝支付需要使用 my.tradePay
        // 但在微信小程序中无法直接调用支付宝，需要跳转到H5页面
        if (paymentResult.data.payUrl) {
          // 跳转到支付宝H5支付页面
          wx.navigateTo({
            url: `/pages/webview/webview?url=${encodeURIComponent(paymentResult.data.payUrl)}`
          })
          
          // 监听支付结果
          setTimeout(() => {
            resolve({
              success: true,
              data: paymentResult.data
            })
          }, 1000)
        } else {
          reject({
            success: false,
            message: '获取支付宝支付链接失败'
          })
        }
      } else {
        resolve(paymentResult)
      }
    }).catch(err => {
      reject(err)
    })
  })
}

module.exports = {
  createPayment,
  processPayment,
  getPaymentDetail,
  getPaymentsByOrder,
  wechatPay,
  alipay
}