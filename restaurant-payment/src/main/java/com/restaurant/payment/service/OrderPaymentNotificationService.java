package com.restaurant.payment.service;

/**
 * 订单支付状态通知服务接口
 */
public interface OrderPaymentNotificationService {
    
    /**
     * 更新订单支付状态
     * @param orderId 订单ID
     * @param isPaid 是否已支付
     */
    void updateOrderPaymentStatus(Long orderId, boolean isPaid);
    
    /**
     * 处理支付超时
     * @param orderId 订单ID
     */
    void handlePaymentTimeout(Long orderId);
}