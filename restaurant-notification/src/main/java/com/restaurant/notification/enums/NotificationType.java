package com.restaurant.notification.enums;

/**
 * 通知类型枚举
 */
public enum NotificationType {
    
    ORDER_CREATED("ORDER_CREATED", "新订单"),
    ORDER_CONFIRMED("ORDER_CONFIRMED", "订单确认"),
    ORDER_PREPARING("ORDER_PREPARING", "开始制作"),
    ORDER_READY("ORDER_READY", "制作完成"),
    ORDER_COMPLETED("ORDER_COMPLETED", "订单完成"),
    ORDER_CANCELLED("ORDER_CANCELLED", "订单取消"),
    PAYMENT_SUCCESS("PAYMENT_SUCCESS", "支付成功"),
    PAYMENT_FAILED("PAYMENT_FAILED", "支付失败"),
    TABLE_OCCUPIED("TABLE_OCCUPIED", "桌台占用"),
    TABLE_AVAILABLE("TABLE_AVAILABLE", "桌台空闲"),
    SYSTEM_MAINTENANCE("SYSTEM_MAINTENANCE", "系统维护");
    
    private final String code;
    private final String description;
    
    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static NotificationType fromCode(String code) {
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type code: " + code);
    }
}