package com.restaurant.auth.dto;

/**
 * 登录响应DTO
 */
public class LoginResponse {

    private String token;
    private String userType; // STAFF 或 CUSTOMER
    private Long userId;
    private String username;
    private String role;
    private String realName;

    public LoginResponse() {}

    public LoginResponse(String token, String userType, Long userId, String username, String role, String realName) {
        this.token = token;
        this.userType = userType;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.realName = realName;
    }

    // 便于前端获取显示名称的方法
    public String getDisplayName() {
        return realName != null && !realName.trim().isEmpty() ? realName : username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}