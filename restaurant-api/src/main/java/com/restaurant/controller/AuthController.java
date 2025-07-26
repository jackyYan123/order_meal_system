package com.restaurant.controller;

import com.restaurant.auth.dto.LoginRequest;
import com.restaurant.auth.dto.LoginResponse;
import com.restaurant.auth.service.AuthService;
import com.restaurant.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 员工登录
     */
    @PostMapping("/staff/login")
    public Result<LoginResponse> staffLogin(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.staffLogin(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("LOGIN_FAILED", e.getMessage());
        }
    }

    /**
     * 顾客登录（微信小程序）
     */
    @PostMapping("/customer/login")
    public Result<LoginResponse> customerLogin(@RequestParam String wechatOpenid) {
        try {
            LoginResponse response = authService.customerLogin(wechatOpenid);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("LOGIN_FAILED", e.getMessage());
        }
    }

    /**
     * 小程序登录
     */
    @PostMapping("/miniprogram/login")
    public Result<LoginResponse> miniprogramLogin(@RequestBody Map<String, Object> loginData) {
        try {
            String code = (String) loginData.get("code");
            Map<String, Object> userInfo = (Map<String, Object>) loginData.get("userInfo");
            
            LoginResponse response = authService.miniprogramLogin(code, userInfo);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("LOGIN_FAILED", e.getMessage());
        }
    }

    /**
     * 小程序游客登录
     */
    @PostMapping("/miniprogram/guest-login")
    public Result<LoginResponse> guestLogin(@RequestBody Map<String, Object> loginData) {
        try {
            String code = (String) loginData.get("code");
            LoginResponse response = authService.guestLogin(code);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("LOGIN_FAILED", e.getMessage());
        }
    }

    /**
     * 获取用户信息（小程序兼容接口）
     */
    @GetMapping("/userinfo")
    public Result<Object> getUserInfo(HttpServletRequest request) {
        return getCurrentUser(request);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update-userinfo")
    public Result<Object> updateUserInfo(HttpServletRequest request, @RequestBody Map<String, Object> userInfo) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return Result.error("TOKEN_MISSING", "Token缺失");
            }
            
            Object updatedUser = authService.updateUserInfo(token, userInfo);
            return Result.success(updatedUser);
        } catch (Exception e) {
            return Result.error("UPDATE_FAILED", "更新用户信息失败");
        }
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token != null) {
                authService.logout(token);
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error("LOGOUT_FAILED", "登出失败");
        }
    }

    /**
     * 验证Token
     */
    @GetMapping("/validate")
    public Result<Boolean> validateToken(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return Result.success(false);
            }
            boolean isValid = authService.validateToken(token);
            return Result.success(isValid);
        } catch (Exception e) {
            return Result.success(false);
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    public Result<Object> getCurrentUser(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return Result.error("TOKEN_MISSING", "Token缺失");
            }
            
            Object user = authService.getCurrentUser(token);
            if (user == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在或Token无效");
            }
            
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("GET_USER_FAILED", "获取用户信息失败");
        }
    }

    /**
     * 检查权限
     */
    @GetMapping("/check-permission")
    public Result<Boolean> checkPermission(HttpServletRequest request, @RequestParam String permission) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return Result.success(false);
            }
            
            boolean hasPermission = authService.hasPermission(token, permission);
            return Result.success(hasPermission);
        } catch (Exception e) {
            return Result.success(false);
        }
    }

    /**
     * 从请求中提取Token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}