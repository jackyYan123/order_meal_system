package com.restaurant.auth.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 分配权限请求DTO
 */
public class AssignPermissionRequest {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @NotNull(message = "权限ID列表不能为空")
    private List<Long> permissionIds;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}