package com.restaurant.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单DTO
 */
@Data
public class OrderDto {
    
    private Long id;
    private String orderNo;
    private Long tableId;
    private String tableName;
    private Long customerId;
    private String customerName;
    private String status;
    private String statusDescription;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paymentTime;
    private LocalDateTime estimatedTime;
    private LocalDateTime completedTime;
    private String remarks;
    private String cancelReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<OrderItemDto> items;
    
    @Data
    public static class OrderItemDto {
        private Long id;
        private Long dishId;
        private String dishName;
        private String dishImage;
        private BigDecimal dishPrice;
        private Integer quantity;
        private BigDecimal subtotal;
        private String specialRequests;
    }
}