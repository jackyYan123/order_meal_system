package com.restaurant.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.restaurant.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
public class Order extends BaseEntity {
    
    @TableId
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNumber;
    
    /**
     * 桌台ID
     */
    private Long tableId;

    /**
     * 桌台号码
     */
    @TableField(exist = false)
    private String tableNumber;
    
    /**
     * 顾客ID
     */
    private Long customerId;
    
    /**
     * 订单状态：PENDING-待确认, CONFIRMED-已确认, PREPARING-制作中, READY-制作完成, COMPLETED-已完成, CANCELLED-已取消
     */
    private String status;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 实付金额
     */
    @TableField(exist = false)
    private BigDecimal paidAmount;
    
    /**
     * 支付状态：UNPAID-未支付, PAID-已支付, REFUNDED-已退款
     */
    private String paymentStatus;
    
    /**
     * 支付方式：WECHAT-微信支付, ALIPAY-支付宝, CASH-现金
     */
    private String paymentMethod;
    
    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 预计完成时间
     */
    private LocalDateTime estimatedTime;
    
    /**
     * 实际完成时间
     */
    private LocalDateTime completedTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 取消原因
     */
    private String cancelReason;
}