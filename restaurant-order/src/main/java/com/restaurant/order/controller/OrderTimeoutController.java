package com.restaurant.order.controller;

import com.restaurant.common.result.Result;
import com.restaurant.order.service.OrderTimeoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单超时处理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/orders/timeout")
@RequiredArgsConstructor
public class OrderTimeoutController {
    
    private final OrderTimeoutService orderTimeoutService;
    
    /**
     * 手动触发超时订单处理
     */
    @PostMapping("/process")
    public Result<Map<String, Object>> processTimeoutOrders(@RequestParam(defaultValue = "30") int timeoutMinutes) {
        log.info("手动触发超时订单处理，超时时间: {}分钟", timeoutMinutes);
        
        int processedCount = orderTimeoutService.processTimeoutOrders(timeoutMinutes);
        
        Map<String, Object> result = new HashMap<>();
        result.put("timeoutMinutes", timeoutMinutes);
        result.put("processedCount", processedCount);
        result.put("message", String.format("成功处理了%d个超时订单", processedCount));
        
        return Result.success(result);
    }
    
    /**
     * 获取超时未支付的订单ID列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getTimeoutOrderIds(@RequestParam(defaultValue = "30") int timeoutMinutes) {
        log.info("查询超时订单列表，超时时间: {}分钟", timeoutMinutes);
        
        List<Long> orderIds = orderTimeoutService.getTimeoutUnpaidOrderIds(timeoutMinutes);
        
        Map<String, Object> result = new HashMap<>();
        result.put("timeoutMinutes", timeoutMinutes);
        result.put("orderIds", orderIds);
        result.put("count", orderIds.size());
        
        return Result.success(result);
    }
    
    /**
     * 检查单个订单是否超时并处理
     */
    @PostMapping("/check/{orderId}")
    public Result<Map<String, Object>> checkSingleOrderTimeout(@PathVariable Long orderId) {
        log.info("检查单个订单是否超时，订单ID: {}", orderId);
        
        boolean processed = orderTimeoutService.checkAndProcessSingleOrderTimeout(orderId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("processed", processed);
        result.put("message", processed ? "订单已超时并处理" : "订单未超时或不符合处理条件");
        
        return Result.success(result);
    }
}