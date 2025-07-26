package com.restaurant.order.enums;

/**
 * 支付状态枚举
 */
public enum PaymentStatus {
    
    UNPAID("UNPAID", "未支付"),
    PAID("PAID", "已支付"),
    REFUNDED("REFUNDED", "已退款");
    
    private final String code;
    private final String description;
    
    PaymentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PaymentStatus fromCode(String code) {
        for (PaymentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status code: " + code);
    }
}