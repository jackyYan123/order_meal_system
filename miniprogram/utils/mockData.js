// utils/mockData.js - 模拟数据
/**
 * 模拟菜品分类数据
 */
function getMockCategories() {
  return {
    success: true,
    data: [
      { id: 1, name: '热菜', sortOrder: 1, isActive: true },
      { id: 2, name: '凉菜', sortOrder: 2, isActive: true },
      { id: 3, name: '汤类', sortOrder: 3, isActive: true },
      { id: 4, name: '主食', sortOrder: 4, isActive: true },
      { id: 5, name: '饮品', sortOrder: 5, isActive: true }
    ]
  }
}

/**
 * 模拟菜品数据
 */
function getMockDishes() {
  return {
    success: true,
    data: {
      records: [
        {
          id: 1,
          name: '宫保鸡丁',
          price: 28.00,
          image: '/images/dish1.jpg',
          description: '经典川菜，鸡肉嫩滑，花生香脆',
          categoryId: 1,
          stock: 50,
          isRecommended: true
        },
        {
          id: 2,
          name: '麻婆豆腐',
          price: 18.00,
          image: '/images/dish2.jpg',
          description: '麻辣鲜香，豆腐嫩滑',
          categoryId: 1,
          stock: 30,
          isRecommended: false
        },
        {
          id: 3,
          name: '凉拌黄瓜',
          price: 12.00,
          image: '/images/dish3.jpg',
          description: '清爽开胃，夏日必备',
          categoryId: 2,
          stock: 20,
          isRecommended: false
        },
        {
          id: 4,
          name: '西红柿鸡蛋汤',
          price: 15.00,
          image: '/images/dish4.jpg',
          description: '家常汤品，营养丰富',
          categoryId: 3,
          stock: 25,
          isRecommended: true
        }
      ]
    }
  }
}

/**
 * 模拟推荐菜品
 */
function getMockRecommendedDishes() {
  return {
    success: true,
    data: [
      {
        id: 1,
        name: '宫保鸡丁',
        price: 28.00,
        imageUrl: '/images/dish1.jpg',
        description: '经典川菜，鸡肉嫩滑，花生香脆'
      },
      {
        id: 4,
        name: '西红柿鸡蛋汤',
        price: 15.00,
        imageUrl: '/images/dish4.jpg',
        description: '家常汤品，营养丰富'
      }
    ]
  }
}

/**
 * 模拟热门菜品
 */
function getMockPopularDishes() {
  return {
    success: true,
    data: [
      {
        id: 1,
        name: '宫保鸡丁',
        price: 28.00,
        imageUrl: '/images/dish1.jpg',
        description: '经典川菜，鸡肉嫩滑，花生香脆'
      },
      {
        id: 2,
        name: '麻婆豆腐',
        price: 18.00,
        imageUrl: '/images/dish2.jpg',
        description: '麻辣鲜香，豆腐嫩滑'
      }
    ]
  }
}

/**
 * 模拟订单数据
 */
function getMockOrder(orderId) {
  return {
    success: true,
    data: {
      id: orderId,
      orderNo: 'ORDER' + Date.now(),
      tableName: '桌台01',
      totalAmount: 61.00,
      dishAmount: 58.00,
      serviceAmount: 3.00,
      discountAmount: 0.00,
      status: 'PENDING',
      paymentStatus: 'UNPAID',
      items: [
        {
          dishId: 1,
          dishName: '宫保鸡丁',
          price: 28.00,
          quantity: 1,
          specialRequests: ''
        },
        {
          dishId: 2,
          dishName: '麻婆豆腐',
          price: 18.00,
          quantity: 1,
          specialRequests: '少辣'
        },
        {
          dishId: 3,
          dishName: '凉拌黄瓜',
          price: 12.00,
          quantity: 1,
          specialRequests: ''
        }
      ],
      createdAt: new Date().toISOString()
    }
  }
}

/**
 * 模拟登录响应
 */
function getMockLoginResponse() {
  return {
    success: true,
    data: {
      token: 'mock_token_' + Date.now(),
      userInfo: {
        id: 1,
        nickName: '测试用户',
        avatarUrl: '/images/default-avatar.png'
      }
    }
  }
}

module.exports = {
  getMockCategories,
  getMockDishes,
  getMockRecommendedDishes,
  getMockPopularDishes,
  getMockOrder,
  getMockLoginResponse
}