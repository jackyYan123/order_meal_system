package com.restaurant.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.auth.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 根据用户ID查询权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId, String userType);

    /**
     * 根据角色ID查询权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);

    /**
     * 根据权限代码查询权限
     */
    Permission getByPermissionCode(String permissionCode);

    /**
     * 根据资源路径查询权限
     */
    List<Permission> getByResourcePath(String resourcePath);

    /**
     * 检查用户是否有指定权限
     */
    boolean hasPermission(Long userId, String userType, String permissionCode);

    /**
     * 检查用户是否有访问指定资源的权限
     */
    boolean hasResourcePermission(Long userId, String userType, String resourcePath);

    /**
     * 获取权限树结构
     */
    List<Permission> getPermissionTree();

    /**
     * 创建权限
     */
    Permission createPermission(Permission permission);

    /**
     * 更新权限
     */
    Permission updatePermission(Permission permission);

    /**
     * 删除权限
     */
    void deletePermission(Long permissionId);
}