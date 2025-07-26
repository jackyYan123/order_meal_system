package com.restaurant.notification.enums;

/**
 * 接收者类型枚举
 */
public enum RecipientType {
    
    CUSTOMER("CUSTOMER", "顾客"),
    STAFF("STAFF", "服务员"),
    CHEF("CHEF", "厨师"),
    ADMIN("ADMIN", "管理员"),
    ALL("ALL", "所有人");
    
    private final String code;
    private final String description;
    
    RecipientType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static RecipientType fromCode(String code) {
        for (RecipientType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown recipient type code: " + code);
    }
}