package com.restaurant.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.restaurant.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单项实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_items")
public class OrderItem extends BaseEntity {
    
    @TableId
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 菜品ID
     */
    private Long dishId;
    
    /**
     * 菜品名称（冗余字段，防止菜品信息变更影响历史订单）
     */
    private String dishName;
    
    /**
     * 菜品价格（下单时的价格）
     */
    private BigDecimal dishPrice;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
    
    /**
     * 特殊要求
     */
    private String specialRequests;
}