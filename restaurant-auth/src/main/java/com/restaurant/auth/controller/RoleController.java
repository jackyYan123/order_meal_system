package com.restaurant.auth.controller;

import com.restaurant.auth.annotation.RequirePermission;
import com.restaurant.auth.dto.AssignPermissionRequest;
import com.restaurant.auth.dto.AssignRoleRequest;
import com.restaurant.auth.dto.RoleDto;
import com.restaurant.auth.entity.Role;
import com.restaurant.auth.service.RoleService;
import com.restaurant.common.result.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/auth/roles")
@RequirePermission("ROLE_MANAGE")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取所有角色列表
     */
    @GetMapping
    @RequirePermission("ROLE_VIEW")
    public Result<List<RoleDto>> getAllRoles() {
        List<Role> roles = roleService.list();
        List<RoleDto> roleDtos = roles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return Result.success(roleDtos);
    }

    /**
     * 获取启用的角色列表
     */
    @GetMapping("/enabled")
    @RequirePermission("ROLE_VIEW")
    public Result<List<RoleDto>> getEnabledRoles() {
        List<Role> roles = roleService.getEnabledRoles();
        List<RoleDto> roleDtos = roles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return Result.success(roleDtos);
    }

    /**
     * 根据ID获取角色详情
     */
    @GetMapping("/{id}")
    @RequirePermission("ROLE_VIEW")
    public Result<RoleDto> getRoleById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("ROLE_NOT_FOUND", "角色不存在");
        }
        return Result.success(convertToDto(role));
    }

    /**
     * 创建角色
     */
    @PostMapping
    @RequirePermission("ROLE_CREATE")
    public Result<RoleDto> createRole(@Valid @RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return Result.success(convertToDto(createdRole));
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @RequirePermission("ROLE_UPDATE")
    public Result<RoleDto> updateRole(@PathVariable Long id, @Valid @RequestBody Role role) {
        role.setId(id);
        Role updatedRole = roleService.updateRole(role);
        return Result.success(convertToDto(updatedRole));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @RequirePermission("ROLE_DELETE")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 根据用户ID获取角色列表
     */
    @GetMapping("/user/{userId}")
    @RequirePermission("ROLE_VIEW")
    public Result<List<RoleDto>> getRolesByUserId(@PathVariable Long userId,
                                                 @RequestParam String userType) {
        List<Role> roles = roleService.getRolesByUserId(userId, userType);
        List<RoleDto> roleDtos = roles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return Result.success(roleDtos);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/assign-permissions")
    @RequirePermission("ROLE_ASSIGN_PERMISSION")
    public Result<Void> assignPermissions(@Valid @RequestBody AssignPermissionRequest request) {
        roleService.assignPermissions(request.getRoleId(), request.getPermissionIds());
        return Result.success();
    }

    /**
     * 移除角色权限
     */
    @PostMapping("/remove-permissions")
    @RequirePermission("ROLE_REMOVE_PERMISSION")
    public Result<Void> removePermissions(@Valid @RequestBody AssignPermissionRequest request) {
        roleService.removePermissions(request.getRoleId(), request.getPermissionIds());
        return Result.success();
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assign-user")
    @RequirePermission("USER_ASSIGN_ROLE")
    public Result<Void> assignUserRole(@Valid @RequestBody AssignRoleRequest request) {
        for (Long roleId : request.getRoleIds()) {
            roleService.assignUserRole(request.getUserId(), request.getUserType(), roleId);
        }
        return Result.success();
    }

    /**
     * 移除用户角色
     */
    @PostMapping("/remove-user")
    @RequirePermission("USER_REMOVE_ROLE")
    public Result<Void> removeUserRole(@Valid @RequestBody AssignRoleRequest request) {
        for (Long roleId : request.getRoleIds()) {
            roleService.removeUserRole(request.getUserId(), request.getUserType(), roleId);
        }
        return Result.success();
    }

    /**
     * 转换为DTO
     */
    private RoleDto convertToDto(Role role) {
        RoleDto dto = new RoleDto();
        BeanUtils.copyProperties(role, dto);
        return dto;
    }
}