package com.restaurant.notification.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.restaurant.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notifications")
public class Notification extends BaseEntity {
    
    @TableId
    private Long id;
    
    /**
     * 通知类型：ORDER_CREATED-新订单, ORDER_CONFIRMED-订单确认, ORDER_READY-订单完成, PAYMENT_SUCCESS-支付成功
     */
    private String type;
    
    /**
     * 接收者类型：CUSTOMER-顾客, STAFF-员工, CHEF-厨师, ALL-所有人
     */
    private String recipientType;
    
    /**
     * 接收者ID（可选，用于指定特定用户）
     */
    private Long recipientId;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
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
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
    
    /**
     * 通知渠道：WEBSOCKET-WebSocket, SMS-短信, EMAIL-邮件
     */
    private String channel;
    
    /**
     * 发送状态：PENDING-待发送, SENT-已发送, FAILED-发送失败
     */
    private String status;
    
    /**
     * 发送时间
     */
    private LocalDateTime sentTime;
    
    /**
     * 失败原因
     */
    private String failureReason;
}