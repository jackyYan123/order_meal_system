package com.restaurant.payment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.restaurant.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payments")
public class Payment extends BaseEntity {
    
    @TableId
    private Long id;
    
    /**
     * 支付流水号
     */
    private String paymentNo;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
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
     * 第三方支付平台交易号
     */
    private String thirdPartyTransactionId;
    
    /**
     * 支付完成时间
     */
    private LocalDateTime paidTime;
    
    /**
     * 退款时间
     */
    private LocalDateTime refundTime;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 失败原因
     */
    private String failureReason;
    
    /**
     * 备注
     */
    private String remarks;
}