package com.restaurant.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.restaurant.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@TableName("payments")
public class Payment {
    
    @TableId
    private String id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 支付金额
     */
    private BigDecimal amount;
    
    /**
     * 支付方式：WECHAT-微信支付, ALIPAY-支付宝, CASH-现金
     */
    private String paymentMethod;
    
    /**
     * 支付状态：PENDING-待支付, SUCCESS-支付成功, FAILED-支付失败, REFUNDED-已退款
     */
    private String status;

    /**
     * 交易ID
     */
    private String transactionId;
    
    /**
     * 第三方支付平台交易号
     */
    @TableField(exist = false)
    private String thirdPartyTransactionId;
    
    /**
     * 支付完成时间
     */
    private LocalDateTime paidAt;
    
    /**
     * 退款时间
     */
    @TableField(exist = false)
    private LocalDateTime refundTime;
    
    /**
     * 退款金额
     */
    @TableField(exist = false)
    private BigDecimal refundAmount;
    
    /**
     * 失败原因
     */
    @TableField(exist = false)
    private String failureReason;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}