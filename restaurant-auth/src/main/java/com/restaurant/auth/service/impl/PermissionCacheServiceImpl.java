package com.restaurant.auth.service.impl;

import com.restaurant.auth.service.PermissionCacheService;
import com.restaurant.auth.service.PermissionService;
import com.restaurant.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限缓存服务实现类
 */
@Service
public class PermissionCacheServiceImpl implements PermissionCacheService {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_PERMISSIONS_KEY = "auth:permissions:";
    private static final String USER_ROLES_KEY = "auth:roles:";
    private static final long CACHE_EXPIRE_TIME = 30; // 30分钟过期

    @Override
    public List<String> getUserPermissions(Long userId, String userType) {
        String cacheKey = USER_PERMISSIONS_KEY + userType + ":" + userId;
        
        @SuppressWarnings("unchecked")
        List<String> cachedPermissions = (List<String>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedPermissions != null) {
            return cachedPermissions;
        }

        // 从数据库获取权限
        List<String> permissions = permissionService.getPermissionsByUserId(userId, userType)
                .stream()
                .map(permission -> permission.getPermissionCode())
                .collect(Collectors.toList());

        // 缓存权限列表
        redisTemplate.opsForValue().set(cacheKey, permissions, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        
        return permissions;
    }

    @Override
    public List<String> getUserRoles(Long userId, String userType) {
        String cacheKey = USER_ROLES_KEY + userType + ":" + userId;
        
        @SuppressWarnings("unchecked")
        List<String> cachedRoles = (List<String>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedRoles != null) {
            return cachedRoles;
        }

        // 从数据库获取角色
        List<String> roles = roleService.getRolesByUserId(userId, userType)
                .stream()
                .map(role -> role.getRoleCode())
                .collect(Collectors.toList());

        // 缓存角色列表
        redisTemplate.opsForValue().set(cacheKey, roles, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        
        return roles;
    }

    @Override
    public boolean hasPermission(Long userId, String userType, String permissionCode) {
        List<String> userPermissions = getUserPermissions(userId, userType);
        return userPermissions.contains(permissionCode);
    }

    @Override
    public void clearUserPermissionCache(Long userId, String userType) {
        String permissionKey = USER_PERMISSIONS_KEY + userType + ":" + userId;
        String roleKey = USER_ROLES_KEY + userType + ":" + userId;
        
        redisTemplate.delete(permissionKey);
        redisTemplate.delete(roleKey);
    }

    @Override
    public void clearAllPermissionCache() {
        // 清除所有权限相关缓存
        redisTemplate.delete(redisTemplate.keys(USER_PERMISSIONS_KEY + "*"));
        redisTemplate.delete(redisTemplate.keys(USER_ROLES_KEY + "*"));
    }

    @Override
    public void refreshUserPermissionCache(Long userId, String userType) {
        // 先清除缓存
        clearUserPermissionCache(userId, userType);
        
        // 重新加载缓存
        getUserPermissions(userId, userType);
        getUserRoles(userId, userType);
    }
}