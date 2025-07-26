package com.restaurant.auth.util;

import com.restaurant.auth.service.PermissionCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限工具类
 * 提供便捷的权限检查方法
 */
@Component
public class PermissionUtil {

    @Autowired
    private PermissionCacheService permissionCacheService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 检查当前用户是否有指定权限
     */
    public boolean hasPermission(String token, String permissionCode) {
        if (token == null || permissionCode == null) {
            return false;
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            String userType = "CUSTOMER".equals(role) ? "CUSTOMER" : "STAFF";
            
            return permissionCacheService.hasPermission(userId, userType, permissionCode);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查当前用户是否有指定角色
     */
    public boolean hasRole(String token, String roleCode) {
        if (token == null || roleCode == null) {
            return false;
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            String userType = "CUSTOMER".equals(role) ? "CUSTOMER" : "STAFF";
            
            List<String> userRoles = permissionCacheService.getUserRoles(userId, userType);
            return userRoles.contains(roleCode);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查当前用户是否有任意一个指定权限
     */
    public boolean hasAnyPermission(String token, String... permissionCodes) {
        if (token == null || permissionCodes == null || permissionCodes.length == 0) {
            return false;
        }

        for (String permissionCode : permissionCodes) {
            if (hasPermission(token, permissionCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否有所有指定权限
     */
    public boolean hasAllPermissions(String token, String... permissionCodes) {
        if (token == null || permissionCodes == null || permissionCodes.length == 0) {
            return false;
        }

        for (String permissionCode : permissionCodes) {
            if (!hasPermission(token, permissionCode)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前用户是否有任意一个指定角色
     */
    public boolean hasAnyRole(String token, String... roleCodes) {
        if (token == null || roleCodes == null || roleCodes.length == 0) {
            return false;
        }

        for (String roleCode : roleCodes) {
            if (hasRole(token, roleCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前用户的所有权限
     */
    public List<String> getCurrentUserPermissions(String token) {
        if (token == null) {
            return java.util.Collections.emptyList();
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            String userType = "CUSTOMER".equals(role) ? "CUSTOMER" : "STAFF";
            
            return permissionCacheService.getUserPermissions(userId, userType);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 获取当前用户的所有角色
     */
    public List<String> getCurrentUserRoles(String token) {
        if (token == null) {
            return java.util.Collections.emptyList();
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            String userType = "CUSTOMER".equals(role) ? "CUSTOMER" : "STAFF";
            
            return permissionCacheService.getUserRoles(userId, userType);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}