package com.restaurant.payment.service;

import com.restaurant.payment.entity.Payment;

import java.util.Map;

/**
 * 微信支付服务接口
 */
public interface WechatPayService {
    
    /**
     * 创建微信支付
     */
    Map<String, Object> createPayment(Payment payment);
    
    /**
     * 查询支付状态
     */
    Map<String, Object> queryPayment(String paymentNo);
    
    /**
     * 申请退款
     */
    void refund(Payment payment, String reason);
    
    /**
     * 处理支付回调
     */
    void handlePaymentCallback(Map<String, Object> callbackData);
}