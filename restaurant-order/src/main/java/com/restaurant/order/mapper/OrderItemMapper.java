package com.restaurant.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单项Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    /**
     * 根据订单ID查询订单项
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 批量插入订单项
     */
    int insertBatch(@Param("items") List<OrderItem> items);
    
    /**
     * 根据菜品ID统计销量
     */
    Integer countSalesByDishId(@Param("dishId") Long dishId);
}