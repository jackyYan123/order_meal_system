package com.restaurant.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.auth.entity.Permission;
import com.restaurant.auth.mapper.PermissionMapper;
import com.restaurant.auth.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public List<Permission> getPermissionsByUserId(Long userId, String userType) {
        if (userId == null || userType == null) {
            return new ArrayList<>();
        }
        return baseMapper.selectPermissionsByUserId(userId, userType);
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return baseMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public Permission getByPermissionCode(String permissionCode) {
        if (permissionCode == null || permissionCode.trim().isEmpty()) {
            return null;
        }
        return baseMapper.selectByPermissionCode(permissionCode);
    }

    @Override
    public List<Permission> getByResourcePath(String resourcePath) {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return baseMapper.selectByResourcePath(resourcePath);
    }

    @Override
    public boolean hasPermission(Long userId, String userType, String permissionCode) {
        if (userId == null || userType == null || permissionCode == null) {
            return false;
        }
        
        List<Permission> permissions = getPermissionsByUserId(userId, userType);
        return permissions.stream()
                .anyMatch(permission -> permissionCode.equals(permission.getPermissionCode()));
    }

    @Override
    public boolean hasResourcePermission(Long userId, String userType, String resourcePath) {
        if (userId == null || userType == null || resourcePath == null) {
            return false;
        }
        
        List<Permission> permissions = getPermissionsByUserId(userId, userType);
        return permissions.stream()
                .anyMatch(permission -> resourcePath.matches(permission.getResourcePath()));
    }

    @Override
    public List<Permission> getPermissionTree() {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_enabled", true)
                   .orderByAsc("sort_order");
        
        List<Permission> allPermissions = this.list(queryWrapper);
        
        if (CollectionUtils.isEmpty(allPermissions)) {
            return new ArrayList<>();
        }
        
        // 构建权限树
        return buildPermissionTree(allPermissions, null);
    }

    @Override
    public Permission createPermission(Permission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("权限信息不能为空");
        }
        
        // 检查权限代码是否已存在
        Permission existingPermission = getByPermissionCode(permission.getPermissionCode());
        if (existingPermission != null) {
            throw new RuntimeException("权限代码已存在: " + permission.getPermissionCode());
        }
        
        // 设置默认值
        if (permission.getIsEnabled() == null) {
            permission.setIsEnabled(true);
        }
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }
        
        this.save(permission);
        return permission;
    }

    @Override
    public Permission updatePermission(Permission permission) {
        if (permission == null || permission.getId() == null) {
            throw new IllegalArgumentException("权限信息或ID不能为空");
        }
        
        Permission existingPermission = this.getById(permission.getId());
        if (existingPermission == null) {
            throw new RuntimeException("权限不存在");
        }
        
        // 检查权限代码是否与其他权限冲突
        Permission codeCheck = getByPermissionCode(permission.getPermissionCode());
        if (codeCheck != null && !codeCheck.getId().equals(permission.getId())) {
            throw new RuntimeException("权限代码已存在: " + permission.getPermissionCode());
        }
        
        this.updateById(permission);
        return permission;
    }

    @Override
    public void deletePermission(Long permissionId) {
        if (permissionId == null) {
            throw new IllegalArgumentException("权限ID不能为空");
        }
        
        Permission permission = this.getById(permissionId);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        
        // 检查是否有子权限
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", permissionId);
        long childCount = this.count(queryWrapper);
        
        if (childCount > 0) {
            throw new RuntimeException("存在子权限，无法删除");
        }
        
        this.removeById(permissionId);
    }

    /**
     * 构建权限树
     */
    private List<Permission> buildPermissionTree(List<Permission> allPermissions, Long parentId) {
        List<Permission> result = new ArrayList<>();
        
        for (Permission permission : allPermissions) {
            if ((parentId == null && permission.getParentId() == null) ||
                (parentId != null && parentId.equals(permission.getParentId()))) {
                
                // 递归构建子权限
                List<Permission> children = buildPermissionTree(allPermissions, permission.getId());
                // 这里可以设置children属性，如果Permission实体有children字段的话
                
                result.add(permission);
            }
        }
        
        return result;
    }
}