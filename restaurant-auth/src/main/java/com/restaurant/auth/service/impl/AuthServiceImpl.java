package com.restaurant.auth.service.impl;

import com.restaurant.auth.dto.LoginRequest;
import com.restaurant.auth.dto.LoginResponse;
import com.restaurant.auth.entity.Customer;
import com.restaurant.auth.entity.StaffUser;
import com.restaurant.auth.service.AuthService;
import com.restaurant.auth.service.CustomerService;
import com.restaurant.auth.service.PermissionService;
import com.restaurant.auth.service.RoleService;
import com.restaurant.auth.service.StaffUserService;
import com.restaurant.auth.util.JwtUtil;
import com.restaurant.common.exception.BusinessException;
import com.restaurant.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String TOKEN_BLACKLIST_PREFIX = "auth:blacklist:";
    private static final String USER_SESSION_PREFIX = "auth:session:";

    @Override
    public LoginResponse staffLogin(LoginRequest request) {
        // 验证用户名和密码
        StaffUser user = staffUserService.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户名或密码错误");
        }

        if (!user.getIsActive()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户已被禁用");
        }

        if (!staffUserService.validatePassword(request.getUsername(), request.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "用户名或密码错误");
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 将用户会话信息存储到Redis
        String sessionKey = USER_SESSION_PREFIX + user.getId();
        redisTemplate.opsForValue().set(sessionKey, token, 24, TimeUnit.HOURS);

        return new LoginResponse(token, "STAFF", user.getId(), user.getUsername(), 
                                user.getRole(), user.getRealName());
    }

    @Override
    public LoginResponse customerLogin(String wechatOpenid) {
        if (wechatOpenid == null || wechatOpenid.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "微信OpenID不能为空");
        }

        // 查找或创建顾客
        Customer customer = customerService.findByWechatOpenid(wechatOpenid);
        if (customer == null) {
            // 创建新顾客
            customer = new Customer();
            customer.setWechatOpenid(wechatOpenid);
            customer.setMemberLevel("NORMAL");
            customer.setTotalOrders(0);
            customer.setTotalAmount(java.math.BigDecimal.ZERO);
            customerService.save(customer);
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(customer.getId(), wechatOpenid, "CUSTOMER");

        // 将用户会话信息存储到Redis
        String sessionKey = USER_SESSION_PREFIX + customer.getId();
        redisTemplate.opsForValue().set(sessionKey, token, 24, TimeUnit.HOURS);

        return new LoginResponse(token, "CUSTOMER", customer.getId(), wechatOpenid, 
                                "CUSTOMER", customer.getName());
    }

    @Override
    public void logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }

        try {
            // 验证Token格式
            if (!jwtUtil.isValidToken(token)) {
                return;
            }

            // 将Token加入黑名单
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
            long expiration = jwtUtil.getExpirationDateFromToken(token).getTime() - System.currentTimeMillis();
            if (expiration > 0) {
                redisTemplate.opsForValue().set(blacklistKey, "blacklisted", expiration, TimeUnit.MILLISECONDS);
            }

            // 清除用户会话
            Long userId = jwtUtil.getUserIdFromToken(token);
            String sessionKey = USER_SESSION_PREFIX + userId;
            redisTemplate.delete(sessionKey);

        } catch (Exception e) {
            // 忽略Token解析错误，静默处理
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            // 检查Token格式
            if (!jwtUtil.isValidToken(token)) {
                return false;
            }

            // 检查Token是否在黑名单中
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
            if (redisTemplate.hasKey(blacklistKey)) {
                return false;
            }

            // 检查Token是否过期
            if (jwtUtil.isTokenExpired(token)) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Object getCurrentUser(String token) {
        if (!validateToken(token)) {
            return null;
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if ("CUSTOMER".equals(role)) {
                return customerService.getById(userId);
            } else {
                return staffUserService.getById(userId);
            }

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean hasPermission(String token, String permission) {
        if (!validateToken(token)) {
            return false;
        }

        try {
            String role = jwtUtil.getRoleFromToken(token);
            
            // 管理员拥有所有权限
            if ("ADMIN".equals(role)) {
                return true;
            }

            // 根据角色检查权限
            switch (role) {
                case "MANAGER":
                    return checkManagerPermission(permission);
                case "WAITER":
                    return checkWaiterPermission(permission);
                case "CHEF":
                    return checkChefPermission(permission);
                case "CUSTOMER":
                    return checkCustomerPermission(permission);
                default:
                    return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查经理权限
     */
    private boolean checkManagerPermission(String permission) {
        // 经理拥有除系统管理外的所有权限
        return !permission.startsWith("SYSTEM_");
    }

    /**
     * 检查服务员权限
     */
    private boolean checkWaiterPermission(String permission) {
        return permission.equals("ORDER_VIEW") || 
               permission.equals("ORDER_UPDATE") || 
               permission.equals("TABLE_MANAGE") ||
               permission.equals("CUSTOMER_VIEW");
    }

    /**
     * 检查厨师权限
     */
    private boolean checkChefPermission(String permission) {
        return permission.equals("ORDER_VIEW") || 
               permission.equals("ORDER_UPDATE_STATUS");
    }

    /**
     * 检查顾客权限
     */
    private boolean checkCustomerPermission(String permission) {
        return permission.equals("ORDER_CREATE") || 
               permission.equals("ORDER_VIEW_OWN") ||
               permission.equals("MENU_VIEW");
    }

    @Override
    public boolean hasUserPermission(Long userId, String userType, String permissionCode) {
        if (userId == null || userType == null || permissionCode == null) {
            return false;
        }
        return permissionService.hasPermission(userId, userType, permissionCode);
    }

    @Override
    public java.util.List<String> getUserPermissions(Long userId, String userType) {
        if (userId == null || userType == null) {
            return new java.util.ArrayList<>();
        }
        
        return permissionService.getPermissionsByUserId(userId, userType)
                .stream()
                .map(permission -> permission.getPermissionCode())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public java.util.List<String> getUserRoles(Long userId, String userType) {
        if (userId == null || userType == null) {
            return new java.util.ArrayList<>();
        }
        
        return roleService.getRolesByUserId(userId, userType)
                .stream()
                .map(role -> role.getRoleCode())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public LoginResponse miniprogramLogin(String code, java.util.Map<String, Object> userInfo) {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "微信登录code不能为空");
        }

        try {
            // TODO: 实际项目中需要调用微信API，通过code获取openid和session_key
            // 开发阶段：基于用户信息生成相对固定的标识
            String wechatOpenid = generateDevOpenid(userInfo);
            
            // 查找或创建顾客
            Customer customer = customerService.findByWechatOpenid(wechatOpenid);
            if (customer == null) {
                // 创建新顾客
                customer = new Customer();
                customer.setWechatOpenid(wechatOpenid);
                customer.setMemberLevel("NORMAL");
                customer.setTotalOrders(0);
                customer.setTotalAmount(java.math.BigDecimal.ZERO);
                
                // 从userInfo中提取用户信息
                if (userInfo != null) {
                    if (userInfo.containsKey("nickName")) {
                        customer.setName((String) userInfo.get("nickName"));
                    }
                    if (userInfo.containsKey("avatarUrl")) {
                        // 可以保存头像URL到数据库
                    }
                }
                
                customerService.save(customer);
                System.out.println("创建新顾客: " + wechatOpenid + ", 姓名: " + customer.getName());
            } else {
                // 更新现有顾客信息
                if (userInfo != null && userInfo.containsKey("nickName")) {
                    String nickName = (String) userInfo.get("nickName");
                    if (nickName != null && !nickName.trim().isEmpty()) {
                        customer.setName(nickName);
                        customerService.updateById(customer);
                    }
                }
                System.out.println("找到现有顾客: " + wechatOpenid + ", 姓名: " + customer.getName());
            }

            // 生成JWT Token
            String token = jwtUtil.generateToken(customer.getId(), wechatOpenid, "CUSTOMER");

            // 将用户会话信息存储到Redis
            String sessionKey = USER_SESSION_PREFIX + customer.getId();
            redisTemplate.opsForValue().set(sessionKey, token, 24, TimeUnit.HOURS);

            return new LoginResponse(token, "CUSTOMER", customer.getId(), wechatOpenid, 
                                    "CUSTOMER", customer.getName());
            
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信登录失败：" + e.getMessage());
        }
    }

    /**
     * 开发阶段生成相对固定的OpenID
     * 实际项目中应该调用微信API获取真实的OpenID
     */
    private String generateDevOpenid(java.util.Map<String, Object> userInfo) {
        StringBuilder sb = new StringBuilder("dev_wx_");
        
        // 基于用户信息生成相对固定的标识
        if (userInfo != null) {
            String nickName = (String) userInfo.get("nickName");
            String avatarUrl = (String) userInfo.get("avatarUrl");
            
            if (nickName != null && !nickName.trim().isEmpty()) {
                sb.append(nickName.hashCode()).append("_");
            }
            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                sb.append(avatarUrl.hashCode()).append("_");
            }
        }
        
        // 添加时间戳的日期部分（不包含具体时间），这样同一天内是相同的
        sb.append(System.currentTimeMillis() / (24 * 60 * 60 * 1000));
        
        return sb.toString();
    }

    @Override
    public LoginResponse guestLogin(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "登录code不能为空");
        }

        try {
            // 游客登录，创建临时用户
            String guestOpenid = "guest_" + System.currentTimeMillis() + "_" + code.hashCode();
            
            return customerLogin(guestOpenid);
            
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "游客登录失败：" + e.getMessage());
        }
    }

    @Override
    public Object updateUserInfo(String token, java.util.Map<String, Object> userInfo) {
        if (!validateToken(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token无效");
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            if ("CUSTOMER".equals(role)) {
                Customer customer = customerService.getById(userId);
                if (customer == null) {
                    throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
                }

                // 更新顾客信息
                if (userInfo.containsKey("name")) {
                    customer.setName((String) userInfo.get("name"));
                }
                if (userInfo.containsKey("phone")) {
                    customer.setPhone((String) userInfo.get("phone"));
                }

                customerService.updateById(customer);
                return customer;
                
            } else {
                StaffUser staffUser = staffUserService.getById(userId);
                if (staffUser == null) {
                    throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
                }

                // 更新员工信息
                if (userInfo.containsKey("realName")) {
                    staffUser.setRealName((String) userInfo.get("realName"));
                }
                if (userInfo.containsKey("phone")) {
                    staffUser.setPhone((String) userInfo.get("phone"));
                }
                if (userInfo.containsKey("email")) {
                    staffUser.setEmail((String) userInfo.get("email"));
                }

                staffUserService.updateById(staffUser);
                return staffUser;
            }

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户信息失败：" + e.getMessage());
        }
    }
}