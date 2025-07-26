package com.restaurant.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.auth.entity.Role;
import com.restaurant.auth.entity.RolePermission;
import com.restaurant.auth.entity.UserRole;
import com.restaurant.auth.mapper.RoleMapper;
import com.restaurant.auth.service.RoleService;
import com.restaurant.auth.mapper.RolePermissionMapper;
import com.restaurant.auth.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Role> getRolesByUserId(Long userId, String userType) {
        if (userId == null || userType == null) {
            return new ArrayList<>();
        }
        return baseMapper.selectRolesByUserId(userId, userType);
    }

    @Override
    public Role getByRoleCode(String roleCode) {
        if (roleCode == null || roleCode.trim().isEmpty()) {
            return null;
        }
        return baseMapper.selectByRoleCode(roleCode);
    }

    @Override
    public Role createRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("角色信息不能为空");
        }
        
        // 检查角色代码是否已存在
        Role existingRole = getByRoleCode(role.getRoleCode());
        if (existingRole != null) {
            throw new RuntimeException("角色代码已存在: " + role.getRoleCode());
        }
        
        // 设置默认值
        if (role.getIsEnabled() == null) {
            role.setIsEnabled(true);
        }
        if (role.getSortOrder() == null) {
            role.setSortOrder(0);
        }
        
        this.save(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        if (role == null || role.getId() == null) {
            throw new IllegalArgumentException("角色信息或ID不能为空");
        }
        
        Role existingRole = this.getById(role.getId());
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 检查角色代码是否与其他角色冲突
        Role codeCheck = getByRoleCode(role.getRoleCode());
        if (codeCheck != null && !codeCheck.getId().equals(role.getId())) {
            throw new RuntimeException("角色代码已存在: " + role.getRoleCode());
        }
        
        this.updateById(role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        
        Role role = this.getById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 检查是否有用户关联此角色
        QueryWrapper<UserRole> userRoleQuery = new QueryWrapper<>();
        userRoleQuery.eq("role_id", roleId);
        long userCount = userRoleMapper.selectCount(userRoleQuery);
        
        if (userCount > 0) {
            throw new RuntimeException("存在用户关联此角色，无法删除");
        }
        
        // 删除角色权限关联
        QueryWrapper<RolePermission> rolePermissionQuery = new QueryWrapper<>();
        rolePermissionQuery.eq("role_id", roleId);
        rolePermissionMapper.delete(rolePermissionQuery);
        
        // 删除角色
        this.removeById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        
        Role role = this.getById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }
        
        // 删除现有的角色权限关联
        QueryWrapper<RolePermission> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("role_id", roleId);
        rolePermissionMapper.delete(deleteQuery);
        
        // 添加新的角色权限关联
        for (Long permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePermissions(Long roleId, List<Long> permissionIds) {
        if (roleId == null || CollectionUtils.isEmpty(permissionIds)) {
            return;
        }
        
        QueryWrapper<RolePermission> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("role_id", roleId)
                   .in("permission_id", permissionIds);
        rolePermissionMapper.delete(deleteQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRole(Long userId, String userType, Long roleId) {
        if (userId == null || userType == null || roleId == null) {
            throw new IllegalArgumentException("用户ID、用户类型和角色ID不能为空");
        }
        
        Role role = this.getById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 检查是否已存在相同的用户角色关联
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("user_type", userType)
                   .eq("role_id", roleId);
        
        UserRole existingUserRole = userRoleMapper.selectOne(queryWrapper);
        if (existingUserRole != null) {
            return; // 已存在，不重复添加
        }
        
        // 创建用户角色关联
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setUserType(userType);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRole(Long userId, String userType, Long roleId) {
        if (userId == null || userType == null || roleId == null) {
            return;
        }
        
        QueryWrapper<UserRole> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("user_id", userId)
                   .eq("user_type", userType)
                   .eq("role_id", roleId);
        userRoleMapper.delete(deleteQuery);
    }

    @Override
    public List<Role> getEnabledRoles() {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_enabled", true)
                   .orderByAsc("sort_order");
        return this.list(queryWrapper);
    }
}