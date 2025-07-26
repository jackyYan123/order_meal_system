package com.restaurant.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解
 * 用于方法级别的权限控制
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * 权限代码
     */
    String value();
    
    /**
     * 权限描述
     */
    String description() default "";
    
    /**
     * 是否必须登录
     */
    boolean requireLogin() default true;
}