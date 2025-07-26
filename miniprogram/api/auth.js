// api/auth.js - 认证相关API
const { get, post } = require('../utils/request')

/**
 * 小程序登录
 * @param {Object} loginData - 登录数据
 */
function miniprogramLogin(loginData) {
  return post('/auth/miniprogram/login', loginData)
}

/**
 * 游客登录
 * @param {Object} loginData - 登录数据
 */
function guestLogin(loginData) {
  return post('/auth/miniprogram/guest-login', loginData)
}

/**
 * 获取用户信息
 */
function getUserInfo() {
  return get('/auth/userinfo')
}

/**
 * 更新用户信息
 * @param {Object} userInfo - 用户信息
 */
function updateUserInfo(userInfo) {
  return post('/auth/update-userinfo', userInfo)
}

module.exports = {
  miniprogramLogin,
  guestLogin,
  getUserInfo,
  updateUserInfo
}