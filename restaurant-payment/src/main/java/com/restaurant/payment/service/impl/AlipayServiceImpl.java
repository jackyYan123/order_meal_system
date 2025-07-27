package com.restaurant.payment.service.impl;

import com.restaurant.common.exception.BusinessException;
import com.restaurant.common.exception.ErrorCode;
import com.restaurant.payment.entity.Payment;
import com.restaurant.payment.service.AlipayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayServiceImpl implements AlipayService {
    
    private final ApplicationContext applicationContext;
    
    @Value("${alipay.app-id:}")
    private String appId;
    
    @Value("${alipay.private-key:}")
    private String privateKey;
    
    @Value("${alipay.public-key:}")
    private String publicKey;
    
    @Value("${alipay.gateway-url:https://openapi.alipay.com/gateway.do}")
    private String gatewayUrl;
    
    @Value("${alipay.notify-url:}")
    private String notifyUrl;
    
    @Value("${alipay.return-url:}")
    private String returnUrl;
    
    @Override
    public Map<String, Object> createPayment(Payment payment) {
        log.info("创建支付宝支付，支付流水号: {}, 金额: {}", payment.getPaymentNo(), payment.getAmount());
        
        try {
            // 构建支付参数
            Map<String, Object> result = new HashMap<>();
            result.put("paymentMethod", "ALIPAY");
            result.put("paymentNo", payment.getPaymentNo());
            result.put("amount", payment.getAmount());
            
            // 在实际项目中，这里应该调用支付宝SDK生成支付链接
            // 由于这是示例代码，我们模拟生成一个支付链接
            String payUrl = generateAlipayUrl(payment);
            result.put("payUrl", payUrl);
            
            // 支付宝H5支付的其他参数
            result.put("appId", appId);
            result.put("timestamp", String.valueOf(System.currentTimeMillis()));
            result.put("format", "JSON");
            result.put("charset", "utf-8");
            result.put("signType", "RSA2");
            result.put("version", "1.0");
            
            log.info("支付宝支付创建成功，支付流水号: {}", payment.getPaymentNo());
            return result;
            
        } catch (Exception e) {
            log.error("创建支付宝支付失败，支付流水号: {}", payment.getPaymentNo(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建支付宝支付失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> queryPayment(String paymentNo) {
        log.info("查询支付宝支付状态，支付流水号: {}", paymentNo);
        
        try {
            // 在实际项目中，这里应该调用支付宝SDK查询支付状态
            // 由于这是示例代码，我们模拟查询结果
            Map<String, Object> result = new HashMap<>();
            result.put("paymentNo", paymentNo);
            result.put("status", "TRADE_SUCCESS"); // 支付宝的支付成功状态
            result.put("tradeNo", "2024072722001234567890123456"); // 支付宝交易号
            
            return result;
            
        } catch (Exception e) {
            log.error("查询支付宝支付状态失败，支付流水号: {}", paymentNo, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询支付宝支付状态失败: " + e.getMessage());
        }
    }
    
    @Override
    public void refund(Payment payment, String reason) {
        log.info("申请支付宝退款，支付流水号: {}, 退款原因: {}", payment.getPaymentNo(), reason);
        
        try {
            // 在实际项目中，这里应该调用支付宝SDK申请退款
            // 由于这是示例代码，我们模拟退款处理
            
            // 模拟退款成功
            log.info("支付宝退款申请成功，支付流水号: {}", payment.getPaymentNo());
            
        } catch (Exception e) {
            log.error("申请支付宝退款失败，支付流水号: {}", payment.getPaymentNo(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "申请支付宝退款失败: " + e.getMessage());
        }
    }
    
    @Override
    public void handlePaymentCallback(Map<String, Object> callbackData) {
        log.info("处理支付宝支付回调: {}", callbackData);
        
        try {
            // 验证回调签名
            if (!verifyCallback(callbackData)) {
                log.error("支付宝回调签名验证失败");
                return;
            }
            
            String paymentNo = (String) callbackData.get("out_trade_no");
            String tradeStatus = (String) callbackData.get("trade_status");
            String tradeNo = (String) callbackData.get("trade_no");
            
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                // 支付成功 - 使用ApplicationContext获取PaymentService避免循环依赖
                getPaymentService().handlePaymentSuccess(paymentNo, tradeNo);
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                // 支付失败
                getPaymentService().handlePaymentFailure(paymentNo, "交易关闭");
            }
            
        } catch (Exception e) {
            log.error("处理支付宝支付回调失败", e);
        }
    }
    
    /**
     * 生成支付宝支付链接
     */
    private String generateAlipayUrl(Payment payment) {
        // 在实际项目中，这里应该使用支付宝SDK生成真实的支付链接
        // 这里只是示例代码
        StringBuilder url = new StringBuilder();
        url.append(gatewayUrl);
        url.append("?service=alipay.wap.create.direct.pay.by.user");
        url.append("&partner=").append(appId);
        url.append("&_input_charset=utf-8");
        url.append("&notify_url=").append(notifyUrl);
        url.append("&return_url=").append(returnUrl);
        url.append("&out_trade_no=").append(payment.getPaymentNo());
        url.append("&subject=").append("餐厅订单支付");
        url.append("&total_fee=").append(payment.getAmount());
        url.append("&seller_id=").append(appId);
        url.append("&payment_type=1");
        
        return url.toString();
    }
    
    /**
     * 验证支付宝回调签名
     */
    private boolean verifyCallback(Map<String, Object> callbackData) {
        // 在实际项目中，这里应该使用支付宝SDK验证签名
        // 这里只是示例代码，直接返回true
        return true;
    }
    
    /**
     * 获取PaymentService - 避免循环依赖
     */
    private com.restaurant.payment.service.PaymentService getPaymentService() {
        return applicationContext.getBean(com.restaurant.payment.service.PaymentService.class);
    }
}