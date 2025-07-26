package com.restaurant.common.result;

import com.restaurant.common.exception.ErrorCode;

/**
 * 统一响应结果类
 */
public class Result<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;

    private Result() {}

    private Result(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(true, "SUCCESS", "操作成功", null);
    }

    /**
     * 成功响应带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "SUCCESS", "操作成功", data);
    }

    /**
     * 成功响应带消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, "SUCCESS", message, data);
    }

    /**
     * 错误响应
     */
    public static <T> Result<T> error(String code, String message) {
        return new Result<>(false, code, message, null);
    }

    /**
     * 错误响应
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 错误响应带数据
     */
    public static <T> Result<T> error(String code, String message, T data) {
        return new Result<>(false, code, message, data);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}