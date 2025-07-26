package com.restaurant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.auth.entity.StaffUser;
import com.restaurant.auth.service.StaffUserService;
import com.restaurant.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户控制器（员工）
 */
@RestController
@RequestMapping("/staff-users")
public class StaffUserController {

    @Autowired
    private StaffUserService staffUserService;

    /**
     * 获取所有系统用户
     */
    @GetMapping
    public Result<List<StaffUser>> getAllStaffUsers() {
        List<StaffUser> users = staffUserService.list();
        return Result.success(users);
    }

    /**
     * 分页查询系统用户
     */
    @GetMapping("/page")
    public Result<IPage<StaffUser>> getStaffUsersByPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        Page<StaffUser> page = new Page<>(current, size);
        IPage<StaffUser> userPage = staffUserService.page(page);
        return Result.success(userPage);
    }

    /**
     * 根据ID获取系统用户
     */
    @GetMapping("/{id}")
    public Result<StaffUser> getStaffUserById(@PathVariable Long id) {
        StaffUser user = staffUserService.getById(id);
        if (user == null) {
            return Result.error("USER_NOT_FOUND", "用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 根据用户名获取系统用户
     */
    @GetMapping("/username/{username}")
    public Result<StaffUser> getStaffUserByUsername(@PathVariable String username) {
        StaffUser user = staffUserService.findByUsername(username);
        if (user == null) {
            return Result.error("USER_NOT_FOUND", "用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 创建系统用户
     */
    @PostMapping
    public Result<StaffUser> createStaffUser(@RequestBody StaffUser user) {
        boolean saved = staffUserService.save(user);
        if (saved) {
            return Result.success(user);
        }
        return Result.error("CREATE_FAILED", "创建用户失败");
    }

    /**
     * 更新系统用户
     */
    @PutMapping("/{id}")
    public Result<StaffUser> updateStaffUser(@PathVariable Long id, @RequestBody StaffUser user) {
        user.setId(id);
        boolean updated = staffUserService.updateById(user);
        if (updated) {
            return Result.success(user);
        }
        return Result.error("UPDATE_FAILED", "更新用户失败");
    }

    /**
     * 删除系统用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteStaffUser(@PathVariable Long id) {
        boolean deleted = staffUserService.removeById(id);
        if (deleted) {
            return Result.success();
        }
        return Result.error("DELETE_FAILED", "删除用户失败");
    }
}