package com.restaurant.auth.controller;

import com.restaurant.auth.annotation.RequirePermission;
import com.restaurant.auth.dto.PermissionDto;
import com.restaurant.auth.entity.Permission;
import com.restaurant.auth.service.PermissionService;
import com.restaurant.common.result.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/auth/permissions")
@RequirePermission("PERMISSION_MANAGE")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    @RequirePermission("PERMISSION_VIEW")
    public Result<List<PermissionDto>> getPermissionTree() {
        List<Permission> permissions = permissionService.getPermissionTree();
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return Result.success(permissionDtos);
    }

    /**
     * 获取所有权限列表
     */
    @GetMapping
    @RequirePermission("PERMISSION_VIEW")
    public Result<List<PermissionDto>> getAllPermissions() {
        List<Permission> permissions = permissionService.list();
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return Result.success(permissionDtos);
    }

    /**
     * 根据ID获取权限详情
     */
    @GetMapping("/{id}")
    @RequirePermission("PERMISSION_VIEW")
    public Result<PermissionDto> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        if (permission == null) {
            return Result.error("PERMISSION_NOT_FOUND", "权限不存在");
        }
        return Result.success(convertToDto(permission));
    }

    /**
     * 创建权限
     */
    @PostMapping
    @RequirePermission("PERMISSION_CREATE")
    public Result<PermissionDto> createPermission(@Valid @RequestBody Permission permission) {
        Permission createdPermission = permissionService.createPermission(permission);
        return Result.success(convertToDto(createdPermission));
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @RequirePermission("PERMISSION_UPDATE")
    public Result<PermissionDto> updatePermission(@PathVariable Long id, 
                                                 @Valid @RequestBody Permission permission) {
        permission.setId(id);
        Permission updatedPermission = permissionService.updatePermission(permission);
        return Result.success(convertToDto(updatedPermission));
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @RequirePermission("PERMISSION_DELETE")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }

    /**
     * 根据用户ID获取权限列表
     */
    @GetMapping("/user/{userId}")
    @RequirePermission("PERMISSION_VIEW")
    public Result<List<PermissionDto>> getPermissionsByUserId(@PathVariable Long userId,
                                                             @RequestParam String userType) {
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId, userType);
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return Result.success(permissionDtos);
    }

    /**
     * 根据角色ID获取权限列表
     */
    @GetMapping("/role/{roleId}")
    @RequirePermission("PERMISSION_VIEW")
    public Result<List<PermissionDto>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getPermissionsByRoleId(roleId);
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return Result.success(permissionDtos);
    }

    /**
     * 检查用户权限
     */
    @GetMapping("/check")
    @RequirePermission("PERMISSION_VIEW")
    public Result<Boolean> checkPermission(@RequestParam Long userId,
                                          @RequestParam String userType,
                                          @RequestParam String permissionCode) {
        boolean hasPermission = permissionService.hasPermission(userId, userType, permissionCode);
        return Result.success(hasPermission);
    }

    /**
     * 转换为DTO
     */
    private PermissionDto convertToDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        BeanUtils.copyProperties(permission, dto);
        return dto;
    }
}