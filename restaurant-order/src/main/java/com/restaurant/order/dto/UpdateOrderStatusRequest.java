package com.restaurant.order.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新订单状态请求DTO
 */
@Data
public class UpdateOrderStatusRequest {
    
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    /**
     * 新状态
     */
    @NotBlank(message = "状态不能为空")
    private String status;
    
    /**
     * 操作备注
     */
    private String remarks;
    
    /**
     * 取消原因（当状态为CANCELLED时必填）
     */
    private String cancelReason;
}