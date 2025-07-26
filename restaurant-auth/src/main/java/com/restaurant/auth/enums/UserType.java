package com.restaurant.auth.enums;

/**
 * 用户类型枚举
 */
public enum UserType {
    
    /**
     * 员工用户
     */
    STAFF("STAFF", "员工"),
    
    /**
     * 顾客用户
     */
    CUSTOMER("CUSTOMER", "顾客");

    private final String code;
    private final String description;

    UserType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static UserType fromCode(String code) {
        for (UserType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的用户类型: " + code);
    }
}