package com.restaurant.order.service.impl;

import com.restaurant.order.entity.Order;
import com.restaurant.order.enums.OrderStatus;
import com.restaurant.order.enums.PaymentStatus;
import com.restaurant.order.mapper.OrderMapper;
import com.restaurant.order.service.OrderService;
import com.restaurant.order.service.OrderTimeoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单超时处理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTimeoutServiceImpl implements OrderTimeoutService {
    
    private final OrderMapper orderMapper;
    private final OrderService orderService;
    
    @Override
    public List<Long> getTimeoutUnpaidOrderIds(int timeoutMinutes) {
        log.debug("查询{}分钟前的超时未支付订单", timeoutMinutes);
        
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
        List<Order> timeoutOrders = orderMapper.selectTimeoutUnpaidOrders(timeoutTime);
        
        List<Long> orderIds = timeoutOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        log.info("发现{}个超时未支付订单", orderIds.size());
        return orderIds;
    }
    
    @Override
    public int processTimeoutOrders(int timeoutMinutes) {
        log.info("开始处理{}分钟前的超时订单", timeoutMinutes);
        
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
        List<Order> timeoutOrders = orderMapper.selectTimeoutUnpaidOrders(timeoutTime);
        
        if (timeoutOrders.isEmpty()) {
            log.debug("没有发现超时订单");
            return 0;
        }
        
        int processedCount = 0;
        for (Order order : timeoutOrders) {
            try {
                log.info("处理超时订单，订单号: {}, 创建时间: {}", 
                        order.getOrderNumber(), order.getCreatedAt());
                
                orderService.handlePaymentTimeout(order.getId());
                processedCount++;
                
            } catch (Exception e) {
                log.error("处理超时订单失败，订单ID: {}, 订单号: {}", 
                        order.getId(), order.getOrderNumber(), e);
            }
        }
        
        log.info("超时订单处理完成，总数: {}, 成功处理: {}", timeoutOrders.size(), processedCount);
        return processedCount;
    }
    
    @Override
    public boolean checkAndProcessSingleOrderTimeout(Long orderId) {
        log.debug("检查单个订单是否超时，订单ID: {}", orderId);
        
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            log.warn("订单不存在，订单ID: {}", orderId);
            return false;
        }
        
        // 检查订单状态
        if (!OrderStatus.PENDING.getCode().equals(order.getStatus()) || 
            !PaymentStatus.UNPAID.getCode().equals(order.getPaymentStatus())) {
            log.debug("订单状态不符合超时处理条件，订单ID: {}, 状态: {}, 支付状态: {}", 
                    orderId, order.getStatus(), order.getPaymentStatus());
            return false;
        }
        
        // 检查是否超时（默认30分钟）
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(30);
        if (order.getCreatedAt().isAfter(timeoutTime)) {
            log.debug("订单未超时，订单ID: {}, 创建时间: {}", orderId, order.getCreatedAt());
            return false;
        }
        
        try {
            log.info("处理超时订单，订单ID: {}, 订单号: {}", orderId, order.getOrderNumber());
            orderService.handlePaymentTimeout(orderId);
            return true;
        } catch (Exception e) {
            log.error("处理超时订单失败，订单ID: {}", orderId, e);
            return false;
        }
    }
}