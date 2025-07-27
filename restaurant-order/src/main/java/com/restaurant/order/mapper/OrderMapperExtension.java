package com.restaurant.order.mapper;

import com.restaurant.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper扩展接口
 */
@Mapper
public interface OrderMapperExtension {
    
    /**
     * 查询超时未支付的订单
     * @param timeoutTime 超时时间点
     * @return 超时订单列表
     */
    @Select("SELECT * FROM orders WHERE status = 'PENDING' AND payment_status = 'UNPAID' AND created_at < #{timeoutTime}")
    List<Order> selectTimeoutUnpaidOrders(@Param("timeoutTime") LocalDateTime timeoutTime);
}