package com.restaurant.auth.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 分配角色请求DTO
 */
public class AssignRoleRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "用户类型不能为空")
    private String userType;

    @NotNull(message = "角色ID列表不能为空")
    private List<Long> roleIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}