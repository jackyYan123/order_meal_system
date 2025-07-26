package com.restaurant.common.exception;

/**
 * 错误码枚举
 */
public enum ErrorCode {

    // 系统错误
    NOT_FOUND("NOT_FOUND", "资源未找到"),
    SYSTEM_ERROR("SYSTEM_ERROR", "系统错误"),
    PARAM_ERROR("PARAM_ERROR", "参数错误"),
    INVALID_PARAMETER("INVALID_PARAMETER", "参数无效"),
    BUSINESS_ERROR("BUSINESS_ERROR", "业务错误"),
    
    // 认证相关错误
    UNAUTHORIZED("UNAUTHORIZED", "未授权访问"),
    FORBIDDEN("FORBIDDEN", "访问被禁止"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "令牌已过期"),
    TOKEN_INVALID("TOKEN_INVALID", "令牌无效"),
    PERMISSION_DENIED("PERMISSION_DENIED", "权限不足"),
    
    // 用户相关错误
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "用户已存在"),
    PASSWORD_ERROR("PASSWORD_ERROR", "密码错误"),
    
    // 菜品相关错误
    DISH_NOT_FOUND("DISH_NOT_FOUND", "菜品不存在"),
    DISH_OUT_OF_STOCK("DISH_OUT_OF_STOCK", "菜品库存不足"),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "分类不存在"),
    
    // 通用资源错误
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "资源不存在"),
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "资源已存在"),
    
    // 订单相关错误
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "订单不存在"),
    ORDER_STATUS_ERROR("ORDER_STATUS_ERROR", "订单状态异常"),
    ORDER_CANNOT_CANCEL("ORDER_CANNOT_CANCEL", "订单无法取消"),
    
    // 桌台相关错误
    TABLE_NOT_FOUND("TABLE_NOT_FOUND", "桌台不存在"),
    TABLE_NOT_AVAILABLE("TABLE_NOT_AVAILABLE", "桌台不可用"),
    TABLE_NUMBER_EXISTS("TABLE_NUMBER_EXISTS", "桌台号已存在"),
    TABLE_CREATE_FAILED("TABLE_CREATE_FAILED", "桌台创建失败"),
    TABLE_UPDATE_FAILED("TABLE_UPDATE_FAILED", "桌台更新失败"),
    TABLE_DELETE_FAILED("TABLE_DELETE_FAILED", "桌台删除失败"),
    TABLE_STATUS_UPDATE_FAILED("TABLE_STATUS_UPDATE_FAILED", "桌台状态更新失败"),
    INVALID_TABLE_STATUS("INVALID_TABLE_STATUS", "无效的桌台状态"),
    TABLE_IN_USE_CANNOT_DELETE("TABLE_IN_USE_CANNOT_DELETE", "桌台使用中，无法删除"),
    
    // 支付相关错误
    PAYMENT_FAILED("PAYMENT_FAILED", "支付失败"),
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", "支付记录不存在"),
    REFUND_FAILED("REFUND_FAILED", "退款失败");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}