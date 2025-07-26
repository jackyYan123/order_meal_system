package com.restaurant.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.auth.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询权限列表
     */
    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN user_roles ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND ur.user_type = #{userType} " +
            "AND p.is_enabled = 1 AND p.deleted = 0")
    List<Permission> selectPermissionsByUserId(Long userId, String userType);

    /**
     * 根据角色ID查询权限列表
     */
    @Select("SELECT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} " +
            "AND p.is_enabled = 1 AND p.deleted = 0 " +
            "ORDER BY p.sort_order ASC")
    List<Permission> selectPermissionsByRoleId(Long roleId);

    /**
     * 根据权限代码查询权限
     */
    @Select("SELECT * FROM permissions WHERE permission_code = #{permissionCode} AND deleted = 0")
    Permission selectByPermissionCode(String permissionCode);

    /**
     * 根据资源路径查询权限
     */
    @Select("SELECT * FROM permissions WHERE resource_path = #{resourcePath} AND deleted = 0")
    List<Permission> selectByResourcePath(String resourcePath);
}