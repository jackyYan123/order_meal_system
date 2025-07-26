package com.restaurant.notification.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建通知请求DTO
 */
@Data
public class CreateNotificationRequest {
    
    /**
     * 通知类型
     */
    @NotBlank(message = "通知类型不能为空")
    private String type;
    
    /**
     * 接收者类型
     */
    @NotBlank(message = "接收者类型不能为空")
    private String recipientType;
    
    /**
     * 接收者ID（可选）
     */
    private Long recipientId;
    
    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    private String title;
    
    /**
     * 通知内容
     */
    @NotBlank(message = "通知内容不能为空")
    private String content;
    
    /**
     * 关联的订单ID
     */
    private Long orderId;
    
    /**
     * 关联的桌台ID
     */
    private Long tableId;
    
    /**
     * 通知渠道
     */
    private String channel;
}