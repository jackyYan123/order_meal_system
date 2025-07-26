package com.restaurant.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.auth.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询角色列表
     */
    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND ur.user_type = #{userType} " +
            "AND r.is_enabled = 1 AND r.deleted = 0")
    List<Role> selectRolesByUserId(Long userId, String userType);

    /**
     * 根据角色代码查询角色
     */
    @Select("SELECT * FROM roles WHERE role_code = #{roleCode} AND deleted = 0")
    Role selectByRoleCode(String roleCode);
}