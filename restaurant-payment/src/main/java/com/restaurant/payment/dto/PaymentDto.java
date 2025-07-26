package com.restaurant.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付DTO
 */
@Data
public class PaymentDto {
    
    private Long id;
    private String paymentNo;
    private Long orderId;
    private String orderNo;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentMethodDescription;
    private String status;
    private String statusDescription;
    private String thirdPartyTransactionId;
    private LocalDateTime paidTime;
    private LocalDateTime refundTime;
    private BigDecimal refundAmount;
    private String failureReason;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}