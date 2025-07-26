package com.restaurant.controller;

import com.restaurant.auth.dto.LoginResponse;
import com.restaurant.auth.service.AuthService;
import com.restaurant.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 小程序控制器
 */
@RestController
@RequestMapping("/miniprogram")
@CrossOrigin(origins = "*")
public class MiniprogramController {

    @Autowired
    private AuthService authService;

    /**
     * 小程序微信登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody Map<String, Object> loginData) {
        try {
            String code = (String) loginData.get("code");
            @SuppressWarnings("unchecked")
            Map<String, Object> userInfo = (Map<String, Object>) loginData.get("userInfo");
            
            if (code == null || code.trim().isEmpty()) {
                return Result.error("PARAM_ERROR", "登录凭证不能为空");
            }
            
            LoginResponse response = authService.miniprogramLogin(code, userInfo);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("LOGIN_FAILED", e.getMessage());
        }
    }

    /**
     * 小程序游客登录
     */
    @PostMapping("/guest-login")
    public Result<LoginResponse> guestLogin(@RequestBody Map<String, Object> loginData) {
        try {
            String code = (String) loginData.get("code");
            
            if (code == null || code.trim().isEmpty()) {
                return Result.error("PARAM_ERROR", "登录凭证不能为空");
            }
            
            LoginResponse response = authService.guestLogin(code);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("LOGIN_FAILED", e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/userinfo")
    public Result<Object> getUserInfo(HttpServletRequest request) {
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
            return Result.error("UPDATE_FAILED", "更新用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 验证Token
     */
    @PostMapping("/validate")
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