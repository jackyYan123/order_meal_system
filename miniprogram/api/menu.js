// api/menu.js - 菜单相关API
const { get } = require('../utils/request')

/**
 * 获取菜品分类列表
 */
function getCategoryList() {
  return get('/api/categories/active')
}

/**
 * 获取菜品列表
 * @param {Object} params - 查询参数
 * @param {number} params.categoryId - 分类ID
 * @param {string} params.keyword - 搜索关键词
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 */
function getDishList(params = {}) {
  if (params.categoryId) {
    return get(`/api/dishes/category/${params.categoryId}/available`, params)
  }
  return get('/api/dishes/available', params)
}

/**
 * 获取菜品详情
 * @param {number} dishId - 菜品ID
 */
function getDishDetail(dishId) {
  return get(`/api/dishes/${dishId}`)
}

/**
 * 搜索菜品
 * @param {string} keyword - 搜索关键词
 */
function searchDishes(keyword) {
  return get('/api/dishes/search', { keyword })
}

/**
 * 获取推荐菜品
 */
function getRecommendedDishes() {
  return get('/api/dishes/recommended')
}

/**
 * 获取热门菜品
 */
function getPopularDishes() {
  return get('/api/dishes/popular')
}

module.exports = {
  getCategoryList,
  getDishList,
  getDishDetail,
  searchDishes,
  getRecommendedDishes,
  getPopularDishes
}