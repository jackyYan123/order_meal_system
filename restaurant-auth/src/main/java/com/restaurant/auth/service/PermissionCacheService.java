package com.restaurant.auth.service;

import java.util.List;

/**
 * 权限缓存服务接口
 * 用于提高权限检查的性能
 */
public interface PermissionCacheService {

    /**
     * 获取用户权限列表（带缓存）
     */
    List<String> getUserPermissions(Long userId, String userType);

    /**
     * 获取用户角色列表（带缓存）
     */
    List<String> getUserRoles(Long userId, String userType);

    /**
     * 检查用户是否有指定权限（带缓存）
     */
    boolean hasPermission(Long userId, String userType, String permissionCode);

    /**
     * 清除用户权限缓存
     */
    void clearUserPermissionCache(Long userId, String userType);

    /**
     * 清除所有权限缓存
     */
    void clearAllPermissionCache();

    /**
     * 刷新用户权限缓存
     */
    void refreshUserPermissionCache(Long userId, String userType);
}