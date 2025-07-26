package com.restaurant.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restaurant.order.dto.CreateOrderRequest;
import com.restaurant.order.dto.OrderDto;
import com.restaurant.order.dto.UpdateOrderStatusRequest;
import com.restaurant.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    OrderDto createOrder(CreateOrderRequest request);
    
    /**
     * 根据ID查询订单详情
     */
    OrderDto getOrderById(Long orderId);
    
    /**
     * 根据订单号查询订单详情
     */
    OrderDto getOrderByOrderNo(String orderNo);
    
    /**
     * 分页查询订单
     */
    IPage<OrderDto> getOrders(int page, int size, String status, Long tableId, 
                             LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 更新订单状态
     */
    void updateOrderStatus(UpdateOrderStatusRequest request);
    
    /**
     * 确认订单
     */
    void confirmOrder(Long orderId, String remarks);
    
    /**
     * 开始制作
     */
    void startPreparing(Long orderId);
    
    /**
     * 制作完成
     */
    void markReady(Long orderId);
    
    /**
     * 完成订单
     */
    void completeOrder(Long orderId);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long orderId, String reason);
    
    /**
     * 根据桌台ID查询进行中的订单
     */
    List<OrderDto> getActiveOrdersByTableId(Long tableId);
    
    /**
     * 查询超时订单
     */
    List<OrderDto> getOverdueOrders();
    
    /**
     * 统计订单数据
     */
    Long countOrdersByStatus(String status, LocalDateTime startTime, LocalDateTime endTime);
}