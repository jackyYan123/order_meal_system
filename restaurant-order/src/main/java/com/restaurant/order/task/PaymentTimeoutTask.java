package com.restaurant.order.task;

import com.restaurant.order.service.OrderTimeoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 支付超时处理定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTimeoutTask {
    
    private final OrderTimeoutService orderTimeoutService;
    
    /**
     * 支付超时时间（分钟），默认30分钟
     */
    @Value("${order.payment.timeout.minutes:30}")
    private int paymentTimeoutMinutes;
    
    /**
     * 每分钟检查一次支付超时订单
     */
    @Scheduled(fixedRate = 60000)
    public void checkPaymentTimeout() {
        try {
            log.debug("开始检查支付超时订单，超时时间: {}分钟", paymentTimeoutMinutes);
            
            // 处理超时未支付的订单
            int processedCount = orderTimeoutService.processTimeoutOrders(paymentTimeoutMinutes);
            
            if (processedCount > 0) {
                log.info("本次处理了{}个超时订单", processedCount);
            }
            
        } catch (Exception e) {
            log.error("检查支付超时订单失败", e);
        }
    }
    
    /**
     * 每小时执行一次深度检查（防止遗漏）
     */
    @Scheduled(fixedRate = 3600000)
    public void deepCheckPaymentTimeout() {
        try {
            log.info("开始执行深度超时订单检查");
            
            // 检查更长时间范围的超时订单（2小时）
            int processedCount = orderTimeoutService.processTimeoutOrders(paymentTimeoutMinutes * 2);
            
            if (processedCount > 0) {
                log.warn("深度检查发现并处理了{}个超时订单", processedCount);
            } else {
                log.info("深度检查未发现超时订单");
            }
            
        } catch (Exception e) {
            log.error("深度检查支付超时订单失败", e);
        }
    }
}