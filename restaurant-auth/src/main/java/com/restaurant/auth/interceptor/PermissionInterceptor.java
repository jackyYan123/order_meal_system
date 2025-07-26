package com.restaurant.auth.interceptor;

import com.restaurant.auth.annotation.RequirePermission;
import com.restaurant.auth.annotation.RequireRole;
import com.restaurant.auth.service.AuthService;
import com.restaurant.auth.service.PermissionService;
import com.restaurant.auth.service.RoleService;
import com.restaurant.auth.util.JwtUtil;
import com.restaurant.common.exception.BusinessException;
import com.restaurant.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 权限拦截器
 * 用于验证用户权限和角色
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 如果不是方法处理器，直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查方法级别的权限注解
        RequirePermission methodPermission = method.getAnnotation(RequirePermission.class);
        RequireRole methodRole = method.getAnnotation(RequireRole.class);

        // 检查类级别的权限注解
        RequirePermission classPermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        RequireRole classRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);

        // 如果没有任何权限注解，直接通过
        if (methodPermission == null && methodRole == null && classPermission == null && classRole == null) {
            return true;
        }

        // 获取Token
        String token = getTokenFromRequest(request);
        
        // 检查是否需要登录
        boolean requireLogin = isRequireLogin(methodPermission, methodRole, classPermission, classRole);
        if (requireLogin && (token == null || !authService.validateToken(token))) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录或登录已过期");
        }

        // 如果不需要登录，直接通过
        if (!requireLogin) {
            return true;
        }

        // 获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        String userRole = jwtUtil.getRoleFromToken(token);
        String userType = "CUSTOMER".equals(userRole) ? "CUSTOMER" : "STAFF";

        // 验证权限
        if (methodPermission != null) {
            validatePermission(userId, userType, methodPermission.value());
        } else if (classPermission != null) {
            validatePermission(userId, userType, classPermission.value());
        }

        // 验证角色
        if (methodRole != null) {
            validateRole(userId, userType, methodRole);
        } else if (classRole != null) {
            validateRole(userId, userType, classRole);
        }

        return true;
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 检查是否需要登录
     */
    private boolean isRequireLogin(RequirePermission methodPermission, RequireRole methodRole,
                                  RequirePermission classPermission, RequireRole classRole) {
        if (methodPermission != null) {
            return methodPermission.requireLogin();
        }
        if (methodRole != null) {
            return methodRole.requireLogin();
        }
        if (classPermission != null) {
            return classPermission.requireLogin();
        }
        if (classRole != null) {
            return classRole.requireLogin();
        }
        return true;
    }

    /**
     * 验证权限
     */
    private void validatePermission(Long userId, String userType, String permissionCode) {
        if (!permissionService.hasPermission(userId, userType, permissionCode)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "权限不足，需要权限: " + permissionCode);
        }
    }

    /**
     * 验证角色
     */
    private void validateRole(Long userId, String userType, RequireRole requireRole) {
        List<String> requiredRoles = Arrays.asList(requireRole.value());
        List<com.restaurant.auth.entity.Role> userRoles = roleService.getRolesByUserId(userId, userType);
        
        List<String> userRoleCodes = userRoles.stream()
                .map(com.restaurant.auth.entity.Role::getRoleCode)
                .collect(java.util.stream.Collectors.toList());

        boolean hasPermission = false;
        
        if (requireRole.logical() == RequireRole.LogicalOperator.AND) {
            // 必须拥有所有角色
            hasPermission = userRoleCodes.containsAll(requiredRoles);
        } else {
            // 拥有其中一个角色即可
            hasPermission = requiredRoles.stream().anyMatch(userRoleCodes::contains);
        }

        if (!hasPermission) {
            throw new BusinessException(ErrorCode.FORBIDDEN, 
                "角色权限不足，需要角色: " + String.join(", ", requiredRoles));
        }
    }
}