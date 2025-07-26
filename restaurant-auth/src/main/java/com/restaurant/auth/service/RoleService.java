package com.restaurant.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.auth.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 根据用户ID查询角色列表
     */
    List<Role> getRolesByUserId(Long userId, String userType);

    /**
     * 根据角色代码查询角色
     */
    Role getByRoleCode(String roleCode);

    /**
     * 创建角色
     */
    Role createRole(Role role);

    /**
     * 更新角色
     */
    Role updateRole(Role role);

    /**
     * 删除角色
     */
    void deleteRole(Long roleId);

    /**
     * 为角色分配权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 移除角色权限
     */
    void removePermissions(Long roleId, List<Long> permissionIds);

    /**
     * 为用户分配角色
     */
    void assignUserRole(Long userId, String userType, Long roleId);

    /**
     * 移除用户角色
     */
    void removeUserRole(Long userId, String userType, Long roleId);

    /**
     * 获取所有启用的角色
     */
    List<Role> getEnabledRoles();
}