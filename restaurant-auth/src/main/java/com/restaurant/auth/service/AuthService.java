package com.restaurant.auth.service;

import com.restaurant.auth.dto.LoginRequest;
import com.restaurant.auth.dto.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 员工登录
     */
    LoginResponse staffLogin(LoginRequest request);

    /**
     * 顾客登录（通过微信OpenID）
     */
    LoginResponse customerLogin(String wechatOpenid);

    /**
     * 登出
     */
    void logout(String token);

    /**
     * 验证Token
     */
    boolean validateToken(String token);

    /**
     * 从Token获取用户信息
     */
    Object getCurrentUser(String token);

    /**
     * 检查权限
     */
    boolean hasPermission(String token, String permission);

    /**
     * 检查用户是否有指定权限
     */
    boolean hasUserPermission(Long userId, String userType, String permissionCode);

    /**
     * 获取用户权限列表
     */
    java.util.List<String> getUserPermissions(Long userId, String userType);

    /**
     * 获取用户角色列表
     */
    java.util.List<String> getUserRoles(Long userId, String userType);

    /**
     * 小程序登录
     */
    LoginResponse miniprogramLogin(String code, java.util.Map<String, Object> userInfo);

    /**
     * 游客登录
     */
    LoginResponse guestLogin(String code);

    /**
     * 更新用户信息
     */
    Object updateUserInfo(String token, java.util.Map<String, Object> userInfo);
}