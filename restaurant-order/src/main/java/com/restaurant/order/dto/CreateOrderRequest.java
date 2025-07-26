package com.restaurant.order.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    
    /**
     * 桌台ID
     */
    @NotNull(message = "桌台ID不能为空")
    private Long tableId;
    
    /**
     * 顾客ID（可选，用于会员识别）
     */
    private Long customerId;
    
    /**
     * 订单项列表
     */
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemRequest> items;
    
    /**
     * 备注
     */
    private String remarks;
    
    @Data
    public static class OrderItemRequest {
        
        /**
         * 菜品ID
         */
        @NotNull(message = "菜品ID不能为空")
        private Long dishId;
        
        /**
         * 数量
         */
        @NotNull(message = "数量不能为空")
        private Integer quantity;
        
        /**
         * 特殊要求
         */
        private String specialRequests;
    }
}