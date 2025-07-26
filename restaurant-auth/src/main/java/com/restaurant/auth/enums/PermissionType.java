package com.restaurant.auth.enums;

/**
 * 权限类型枚举
 */
public enum PermissionType {
    
    /**
     * 菜单权限
     */
    MENU("MENU", "菜单权限"),
    
    /**
     * 按钮权限
     */
    BUTTON("BUTTON", "按钮权限"),
    
    /**
     * API接口权限
     */
    API("API", "接口权限");

    private final String code;
    private final String description;

    PermissionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PermissionType fromCode(String code) {
        for (PermissionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的权限类型: " + code);
    }
}