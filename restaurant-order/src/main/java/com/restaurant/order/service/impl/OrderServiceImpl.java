package com.restaurant.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.common.exception.BusinessException;
import com.restaurant.common.exception.ErrorCode;
import com.restaurant.menu.dto.DishDto;
import com.restaurant.menu.service.DishService;
import com.restaurant.common.service.TableService;
import com.restaurant.common.entity.Table;
import com.restaurant.order.dto.CreateOrderRequest;
import com.restaurant.order.dto.OrderDto;
import com.restaurant.order.dto.UpdateOrderStatusRequest;
import com.restaurant.order.entity.Order;
import com.restaurant.order.entity.OrderItem;
import com.restaurant.order.enums.OrderStatus;
import com.restaurant.order.enums.PaymentStatus;
import com.restaurant.order.mapper.OrderItemMapper;
import com.restaurant.order.mapper.OrderMapper;
import com.restaurant.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final DishService dishService;
    private final TableService tableService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDto createOrder(CreateOrderRequest request) {
        log.info("创建订单，桌台ID: {}", request.getTableId());

        // 检查桌台是否有进行中的订单
        List<Order> activeOrders = orderMapper.selectActiveOrdersByTableId(request.getTableId());
        if (!activeOrders.isEmpty()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "该桌台已有进行中的订单");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNumber(generateOrderNo());
        order.setTableId(request.getTableId());
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.PENDING.getCode());
        order.setPaymentStatus(PaymentStatus.UNPAID.getCode());
        order.setRemark(request.getRemarks());

        // 计算订单总金额
        BigDecimal totalAmount = calculateTotalAmount(request.getItems());
        order.setTotalAmount(totalAmount);

        // 设置预计完成时间（30分钟后）
        order.setEstimatedTime(LocalDateTime.now().plusMinutes(30));

        orderMapper.insert(order);

        // 创建订单项
        List<OrderItem> orderItems = createOrderItems(order.getId(), request.getItems());
        orderItemMapper.insertBatch(orderItems);

        log.info("订单创建成功，订单号: {}", order.getOrderNumber());
        return convertToDto(order, orderItems);
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        return convertToDto(order, items);
    }

    @Override
    public OrderDto getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNumber, orderNo);
        Order order = orderMapper.selectOne(wrapper);

        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        return convertToDto(order, items);
    }

    @Override
    public IPage<OrderDto> getOrders(int page, int size, String status, Long tableId,
            LocalDateTime startTime, LocalDateTime endTime) {
        Page<Order> pageParam = new Page<>(page, size);
        IPage<Order> orderPage = orderMapper.selectOrdersWithDetails(pageParam, status, tableId, startTime, endTime);

        IPage<OrderDto> result = new Page<>(page, size, orderPage.getTotal());
        List<OrderDto> orderDtos = orderPage.getRecords().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
                    return convertToDto(order, items);
                })
                .collect(Collectors.toList());

        result.setRecords(orderDtos);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(UpdateOrderStatusRequest request) {
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        // 验证状态转换是否合法
        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());
        if (OrderStatus.CANCELLED.getCode().equals(request.getStatus())) {
            order.setCancelReason(request.getCancelReason());
        }

        orderMapper.updateById(order);
        log.info("订单状态更新成功，订单号: {}, 新状态: {}", order.getOrderNumber(), request.getStatus());
    }

    @Override
    public void confirmOrder(Long orderId, String remarks) {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderId);
        request.setStatus(OrderStatus.CONFIRMED.getCode());
        request.setRemarks(remarks);
        updateOrderStatus(request);
    }

    @Override
    public void startPreparing(Long orderId) {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderId);
        request.setStatus(OrderStatus.PREPARING.getCode());
        updateOrderStatus(request);
    }

    @Override
    public void markReady(Long orderId) {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderId);
        request.setStatus(OrderStatus.READY.getCode());
        updateOrderStatus(request);
    }

    @Override
    public void completeOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setCompletedTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void cancelOrder(Long orderId, String reason) {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderId);
        request.setStatus(OrderStatus.CANCELLED.getCode());
        request.setCancelReason(reason);
        updateOrderStatus(request);
    }

    @Override
    public List<OrderDto> getActiveOrdersByTableId(Long tableId) {
        List<Order> orders = orderMapper.selectActiveOrdersByTableId(tableId);
        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
                    return convertToDto(order, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getOverdueOrders() {
        List<Order> orders = orderMapper.selectOverdueOrders();
        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
                    return convertToDto(order, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long countOrdersByStatus(String status, LocalDateTime startTime, LocalDateTime endTime) {
        return orderMapper.countOrdersByStatus(status, startTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentStatus(Long orderId, Boolean isPaid, Long paymentTime) {
        log.info("更新订单支付状态，订单ID: {}, 支付状态: {}", orderId, isPaid);
        
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        
        if (isPaid) {
            order.setPaymentStatus(PaymentStatus.PAID.getCode());
            if (paymentTime != null) {
                order.setPaymentTime(LocalDateTime.ofEpochSecond(paymentTime / 1000, 0, java.time.ZoneOffset.ofHours(8)));
            } else {
                order.setPaymentTime(LocalDateTime.now());
            }
            
            // 支付成功后，如果订单状态是待确认，自动确认订单
            if (OrderStatus.PENDING.getCode().equals(order.getStatus())) {
                order.setStatus(OrderStatus.CONFIRMED.getCode());
                log.info("订单支付成功，自动确认订单，订单ID: {}", orderId);
            }
        } else {
            order.setPaymentStatus(PaymentStatus.UNPAID.getCode());
            order.setPaymentTime(null);
        }
        
        orderMapper.updateById(order);
        log.info("订单支付状态更新成功，订单ID: {}, 新状态: {}", orderId, order.getPaymentStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentTimeout(Long orderId) {
        log.info("处理订单支付超时，订单ID: {}", orderId);
        
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            log.warn("订单不存在，订单ID: {}", orderId);
            return;
        }
        
        // 只有待确认且未支付的订单才处理超时
        if (OrderStatus.PENDING.getCode().equals(order.getStatus()) && 
            PaymentStatus.UNPAID.getCode().equals(order.getPaymentStatus())) {
            
            order.setStatus(OrderStatus.CANCELLED.getCode());
            order.setCancelReason("支付超时自动取消");
            
            orderMapper.updateById(order);
            
            // 恢复库存
            List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
            for (OrderItem item : items) {
                try {
                    dishService.restoreStock(item.getDishId(), item.getQuantity());
                } catch (Exception e) {
                    log.error("恢复库存失败，菜品ID: {}, 数量: {}", item.getDishId(), item.getQuantity(), e);
                }
            }
            
            log.info("订单支付超时处理完成，订单已取消，订单ID: {}", orderId);
        } else {
            log.info("订单状态不符合超时取消条件，订单ID: {}, 状态: {}, 支付状态: {}", 
                    orderId, order.getStatus(), order.getPaymentStatus());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleTimeoutOrders() {
        log.debug("开始处理超时订单");
        
        // 查询30分钟前创建的待确认且未支付的订单
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(30);
        
        List<Order> timeoutOrders = orderMapper.selectTimeoutUnpaidOrders(timeoutTime);
        
        if (timeoutOrders.isEmpty()) {
            log.debug("没有发现超时订单");
            return;
        }
        
        log.info("发现 {} 个超时订单，开始处理", timeoutOrders.size());
        
        for (Order order : timeoutOrders) {
            try {
                handlePaymentTimeout(order.getId());
            } catch (Exception e) {
                log.error("处理超时订单失败，订单ID: {}", order.getId(), e);
            }
        }
        
        log.info("超时订单处理完成");
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 1000));
        return "ORD" + timestamp + String.format("%03d", Integer.parseInt(random));
    }

    /**
     * 计算订单总金额
     */
    private BigDecimal calculateTotalAmount(List<CreateOrderRequest.OrderItemRequest> items) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderItemRequest item : items) {
            // 获取菜品信息
            DishDto dish = dishService.getDishById(item.getDishId());
            if (dish == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "菜品不存在，ID: " + item.getDishId());
            }

            // 检查菜品是否可用
            if (!dish.getIsAvailable()) {
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "菜品已下架: " + dish.getName());
            }

            // 检查库存
            if (!dishService.checkStock(item.getDishId(), item.getQuantity())) {
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "菜品库存不足: " + dish.getName());
            }

            // 计算小计
            BigDecimal subtotal = dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        return totalAmount;
    }

    /**
     * 创建订单项
     */
    private List<OrderItem> createOrderItems(Long orderId, List<CreateOrderRequest.OrderItemRequest> items) {
        return items.stream()
                .map(item -> {
                    // 获取菜品信息
                    DishDto dish = dishService.getDishById(item.getDishId());
                    if (dish == null) {
                        throw new BusinessException(ErrorCode.NOT_FOUND, "菜品不存在，ID: " + item.getDishId());
                    }

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(orderId);
                    orderItem.setDishId(item.getDishId());
                    orderItem.setDishName(dish.getName());
                    orderItem.setDishPrice(dish.getPrice());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setSpecialRequests(item.getSpecialRequests());

                    // 计算小计
                    orderItem.setSubtotal(dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

                    // 减少库存
                    dishService.reduceStock(item.getDishId(), item.getQuantity());

                    return orderItem;
                })
                .collect(Collectors.toList());
    }

    /**
     * 验证状态转换是否合法
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        OrderStatus current = OrderStatus.fromCode(currentStatus);
        OrderStatus target = OrderStatus.fromCode(newStatus);

        // 定义合法的状态转换
        boolean isValid = false;
        switch (current) {
            case PENDING:
                isValid = target == OrderStatus.CONFIRMED || target == OrderStatus.CANCELLED;
                break;
            case CONFIRMED:
                isValid = target == OrderStatus.PREPARING || target == OrderStatus.CANCELLED;
                break;
            case PREPARING:
                isValid = target == OrderStatus.READY || target == OrderStatus.CANCELLED;
                break;
            case READY:
                isValid = target == OrderStatus.COMPLETED;
                break;
            case COMPLETED:
            case CANCELLED:
                isValid = false; // 终态不能转换
                break;
        }

        if (!isValid) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR,
                    String.format("订单状态不能从 %s 转换为 %s", current.getDescription(), target.getDescription()));
        }
    }

    /**
     * 转换为DTO
     */
    private OrderDto convertToDto(Order order, List<OrderItem> items) {
        OrderDto dto = new OrderDto();
        BeanUtils.copyProperties(order, dto);

        // 设置订单号
        dto.setOrderNo(order.getOrderNumber());

        // 设置状态描述
        dto.setStatusDescription(OrderStatus.fromCode(order.getStatus()).getDescription());

        // 设置创建时间和更新时间
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // 设置桌台名称
        try {
            Table table = tableService.getById(order.getTableId());
            if (table != null) {
                dto.setTableName(table.getTableNumber());
            }
        } catch (Exception e) {
            log.warn("获取桌台信息失败，桌台ID: {}", order.getTableId(), e);
            dto.setTableName("桌台" + order.getTableId());
        }

        // 转换订单项
        List<OrderDto.OrderItemDto> itemDtos = items.stream()
                .map(item -> {
                    OrderDto.OrderItemDto itemDto = new OrderDto.OrderItemDto();
                    BeanUtils.copyProperties(item, itemDto);

                    // 获取菜品图片信息
                    try {
                        DishDto dish = dishService.getDishById(item.getDishId());
                        if (dish != null) {
                            itemDto.setDishImage(dish.getImageUrl());
                        }
                    } catch (Exception e) {
                        log.warn("获取菜品图片失败，菜品ID: {}", item.getDishId(), e);
                    }

                    return itemDto;
                })
                .collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }
}