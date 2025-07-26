package com.restaurant.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.auth.entity.StaffUser;
import com.restaurant.auth.mapper.StaffUserMapper;
import com.restaurant.auth.service.StaffUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 系统用户服务实现类（员工）
 */
@Service
public class StaffUserServiceImpl extends ServiceImpl<StaffUserMapper, StaffUser> implements StaffUserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public StaffUser findByUsername(String username) {
        QueryWrapper<StaffUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean validatePassword(String username, String password) {
        StaffUser user = findByUsername(username);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public StaffUser createUser(StaffUser user) {
        if (user == null) {
            return null;
        }

        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            user.setPassword(encryptPassword(user.getPassword()));
        }

        // 设置默认值
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        // 保存用户
        this.save(user);
        return user;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || newPassword == null) {
            return false;
        }

        StaffUser user = this.getById(userId);
        if (user == null) {
            return false;
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // 更新新密码
        user.setPassword(encryptPassword(newPassword));
        return this.updateById(user);
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        if (userId == null || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        StaffUser user = this.getById(userId);
        if (user == null) {
            return false;
        }

        // 重置密码
        user.setPassword(encryptPassword(newPassword));
        return this.updateById(user);
    }

    @Override
    public String encryptPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return passwordEncoder.encode(rawPassword);
    }
}