package com.restaurant.order.service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单超时处理服务接口
 */
public interface OrderTimeoutService {
    
    /**
     * 获取超时未支付的订单
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单列表
     */
    List<Long> getTimeoutUnpaidOrderIds(int timeoutMinutes);
    
    /**
     * 批量处理超时订单
     * @param timeoutMinutes 超时分钟数
     * @return 处理的订单数量
     */
    int processTimeoutOrders(int timeoutMinutes);
    
    /**
     * 检查并处理单个订单是否超时
     * @param orderId 订单ID
     * @return 是否处理了超时订单
     */
    boolean checkAndProcessSingleOrderTimeout(Long orderId);
}