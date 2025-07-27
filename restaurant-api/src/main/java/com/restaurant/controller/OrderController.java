package com.restaurant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restaurant.common.result.Result;
import com.restaurant.order.dto.CreateOrderRequest;
import com.restaurant.order.dto.OrderDto;
import com.restaurant.order.dto.UpdateOrderStatusRequest;
import com.restaurant.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("创建订单请求: {}", request);
        OrderDto order = orderService.createOrder(request);
        return Result.success(order);
    }

    /**
     * 根据ID查询订单详情
     */
    @GetMapping("/{orderId}")
    public Result<OrderDto> getOrderById(@PathVariable Long orderId) {
        log.info("查询订单详情，订单ID: {}", orderId);
        OrderDto order = orderService.getOrderById(orderId);
        log.info("查询到订单详情: {}", order);
        return Result.success(order);
    }

    /**
     * 根据订单号查询订单详情
     */
    @GetMapping("/orderNo/{orderNo}")
    public Result<OrderDto> getOrderByOrderNo(@PathVariable String orderNo) {
        log.info("根据订单号查询订单详情，订单号: {}", orderNo);
        OrderDto order = orderService.getOrderByOrderNo(orderNo);
        log.info("查询到订单详情: {}", order);
        return Result.success(order);
    }

    /**
     * 分页查询订单
     */
    @GetMapping
    public Result<IPage<OrderDto>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long tableId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        IPage<OrderDto> orders = orderService.getOrders(page, size, status, tableId, startTime, endTime);
        return Result.success(orders);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/status")
    public Result<Void> updateOrderStatus(@Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("更新订单状态请求: {}", request);
        orderService.updateOrderStatus(request);
        return Result.success();
    }

    /**
     * 确认订单
     */
    @PutMapping("/{orderId}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long orderId, @RequestParam(required = false) String remarks) {
        orderService.confirmOrder(orderId, remarks);
        return Result.success();
    }

    /**
     * 开始制作
     */
    @PutMapping("/{orderId}/prepare")
    public Result<Void> startPreparing(@PathVariable Long orderId) {
        orderService.startPreparing(orderId);
        return Result.success();
    }

    /**
     * 制作完成
     */
    @PutMapping("/{orderId}/ready")
    public Result<Void> markReady(@PathVariable Long orderId) {
        orderService.markReady(orderId);
        return Result.success();
    }

    /**
     * 完成订单
     */
    @PutMapping("/{orderId}/complete")
    public Result<Void> completeOrder(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId, @RequestParam String reason) {
        orderService.cancelOrder(orderId, reason);
        return Result.success();
    }

    /**
     * 根据桌台ID查询进行中的订单
     */
    @GetMapping("/table/{tableId}/active")
    public Result<List<OrderDto>> getActiveOrdersByTableId(@PathVariable Long tableId) {
        List<OrderDto> orders = orderService.getActiveOrdersByTableId(tableId);
        return Result.success(orders);
    }

    /**
     * 查询超时订单
     */
    @GetMapping("/overdue")
    public Result<List<OrderDto>> getOverdueOrders() {
        List<OrderDto> orders = orderService.getOverdueOrders();
        return Result.success(orders);
    }

    /**
     * 统计订单数据
     */
    @GetMapping("/count")
    public Result<Long> countOrdersByStatus(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        Long count = orderService.countOrdersByStatus(status, startTime, endTime);
        return Result.success(count);
    }

    /**
     * 更新订单支付状态
     */
    @PutMapping("/{orderId}/payment-status")
    public Result<Void> updatePaymentStatus(@PathVariable Long orderId, @RequestBody Map<String, Object> request) {
        log.info("更新订单支付状态，订单ID: {}, 请求: {}", orderId, request);
        Boolean isPaid = (Boolean) request.get("isPaid");
        Long paymentTime = (Long) request.get("paymentTime");
        orderService.updatePaymentStatus(orderId, isPaid, paymentTime);
        return Result.success();
    }

    /**
     * 处理订单支付超时
     */
    @PutMapping("/{orderId}/payment-timeout")
    public Result<Void> handlePaymentTimeout(@PathVariable Long orderId) {
        log.info("处理订单支付超时，订单ID: {}", orderId);
        orderService.handlePaymentTimeout(orderId);
        return Result.success();
    }
}