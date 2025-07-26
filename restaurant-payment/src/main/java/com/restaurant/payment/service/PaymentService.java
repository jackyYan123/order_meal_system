package com.restaurant.payment.service;

import com.restaurant.payment.dto.CreatePaymentRequest;
import com.restaurant.payment.dto.PaymentDto;

import java.util.List;
import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 创建支付
     */
    PaymentDto createPayment(CreatePaymentRequest request);
    
    /**
     * 处理支付（调用第三方支付）
     */
    Map<String, Object> processPayment(Long paymentId);
    
    /**
     * 支付成功回调处理
     */
    void handlePaymentSuccess(String paymentNo, String thirdPartyTransactionId);
    
    /**
     * 支付失败回调处理
     */
    void handlePaymentFailure(String paymentNo, String failureReason);
    
    /**
     * 查询支付详情
     */
    PaymentDto getPaymentById(Long paymentId);
    
    /**
     * 根据支付流水号查询支付详情
     */
    PaymentDto getPaymentByPaymentNo(String paymentNo);
    
    /**
     * 根据订单ID查询支付记录
     */
    List<PaymentDto> getPaymentsByOrderId(Long orderId);
    
    /**
     * 申请退款
     */
    void refund(Long paymentId, String reason);
    
    /**
     * 现金支付确认
     */
    void confirmCashPayment(Long paymentId);
}