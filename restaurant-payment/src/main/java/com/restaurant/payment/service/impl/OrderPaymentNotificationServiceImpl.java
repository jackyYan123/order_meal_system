package com.restaurant.payment.service.impl;

import com.restaurant.payment.service.OrderPaymentNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单支付状态通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentNotificationServiceImpl implements OrderPaymentNotificationService {
    
    private final RestTemplate restTemplate;
    
    @Override
    public void updateOrderPaymentStatus(Long orderId, boolean isPaid) {
        log.info("通知订单服务更新支付状态，订单ID: {}, 支付状态: {}", orderId, isPaid);
        
        try {
            String url = "http://localhost:8080/api/orders/" + orderId + "/payment-status";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("isPaid", isPaid);
            requestBody.put("paymentTime", System.currentTimeMillis());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            restTemplate.put(url, request);
            
            log.info("订单支付状态更新通知发送成功，订单ID: {}", orderId);
        } catch (Exception e) {
            log.error("通知订单服务更新支付状态失败，订单ID: {}", orderId, e);
            throw new RuntimeException("更新订单支付状态失败", e);
        }
    }
    
    @Override
    public void handlePaymentTimeout(Long orderId) {
        log.info("处理订单支付超时，订单ID: {}", orderId);
        
        try {
            String url = "http://localhost:8080/api/orders/" + orderId + "/payment-timeout";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            restTemplate.put(url, request);
            
            log.info("订单支付超时处理通知发送成功，订单ID: {}", orderId);
        } catch (Exception e) {
            log.error("处理订单支付超时失败，订单ID: {}", orderId, e);
        }
    }
}