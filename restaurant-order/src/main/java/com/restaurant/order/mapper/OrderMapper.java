package com.restaurant.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 分页查询订单（带关联信息）
     */
    IPage<Order> selectOrdersWithDetails(Page<Order> page, @Param("status") String status, 
                                        @Param("tableId") Long tableId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据桌台ID查询进行中的订单
     */
    List<Order> selectActiveOrdersByTableId(@Param("tableId") Long tableId);
    
    /**
     * 查询需要提醒的订单（超过预计完成时间）
     */
    List<Order> selectOverdueOrders();
    
    /**
     * 统计订单数据
     */
    Long countOrdersByStatus(@Param("status") String status, 
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询超时未支付的订单
     */
    List<Order> selectTimeoutUnpaidOrders(@Param("timeoutTime") LocalDateTime timeoutTime);
}