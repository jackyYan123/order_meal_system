package com.restaurant.order.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    
    PENDING("PENDING", "待确认"),
    CONFIRMED("CONFIRMED", "已确认"),
    PREPARING("PREPARING", "制作中"),
    READY("READY", "制作完成"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消");
    
    private final String code;
    private final String description;
    
    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status code: " + code);
    }
}