package com.restaurant.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色验证注解
 * 用于方法级别的角色控制
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    
    /**
     * 角色代码数组，满足其中一个即可
     */
    String[] value();
    
    /**
     * 角色描述
     */
    String description() default "";
    
    /**
     * 是否必须登录
     */
    boolean requireLogin() default true;
    
    /**
     * 逻辑关系：AND-必须拥有所有角色，OR-拥有其中一个角色即可
     */
    LogicalOperator logical() default LogicalOperator.OR;
    
    enum LogicalOperator {
        AND, OR
    }
}