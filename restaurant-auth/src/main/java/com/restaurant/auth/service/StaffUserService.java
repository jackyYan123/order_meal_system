package com.restaurant.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.auth.entity.StaffUser;

/**
 * 系统用户服务接口（员工）
 */
public interface StaffUserService extends IService<StaffUser> {

    /**
     * 根据用户名查找用户
     */
    StaffUser findByUsername(String username);

    /**
     * 验证用户密码
     */
    boolean validatePassword(String username, String password);

    /**
     * 创建用户（自动加密密码）
     */
    StaffUser createUser(StaffUser user);

    /**
     * 更新用户密码
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置用户密码（管理员操作）
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 加密密码
     */
    String encryptPassword(String rawPassword);
}