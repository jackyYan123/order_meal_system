package com.restaurant.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.payment.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付Mapper
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
    
    /**
     * 根据订单ID查询支付记录
     */
    List<Payment> selectByOrderId(@Param("orderNo") String orderNo);
    
    /**
     * 根据支付流水号查询支付记录
     */
    Payment selectByPaymentNo(@Param("paymentNo") String paymentNo);
    
    /**
     * 查询指定时间范围内的支付统计
     */
    List<Payment> selectPaymentStatistics(@Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("status") String status);
}